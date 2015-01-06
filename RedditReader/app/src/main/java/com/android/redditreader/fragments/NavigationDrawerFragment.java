package com.android.redditreader.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.redditreader.R;
import com.android.redditreader.activities.MainActivity;
import com.android.redditreader.models.Subreddit;
import com.android.redditreader.utils.Globals;
import com.android.redditreader.utils.Helpers;
import com.android.redditreader.utils.RedditApiWrapper;

import java.util.ArrayList;


public class NavigationDrawerFragment extends Fragment {

    private MainActivity mainActivity;
    private RecyclerView subredditsRecyclerView;
    private ProgressBar subredditsProgressBar;
    private NavigationDrawerSubredditsAdapter navigationDrawerSubredditsAdapter;
    Resources res;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        findViews(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        res = mainActivity.getResources();

        // set up subreddits recycler view
        navigationDrawerSubredditsAdapter = new NavigationDrawerSubredditsAdapter(new ArrayList<Subreddit>());
        subredditsRecyclerView.setAdapter(navigationDrawerSubredditsAdapter);
        subredditsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new GetSubredditsTask().execute();
    }

    private void findViews(View view) {
        subredditsRecyclerView = (RecyclerView) view.findViewById(R.id.subreddits_recycler_view);
        subredditsProgressBar = (ProgressBar) view.findViewById(R.id.subreddits_progressbar);
    }

    private class NavigationDrawerSubredditsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final String TAG = NavigationDrawerSubredditsAdapter.class.getSimpleName();

        private final int TYPE_HEADAER = 0;
        private final int TYPE_ITEM = 1;

        public ArrayList<Subreddit> subreddits;

        private int currentPos = 1;
        private int oldPos = 1;

        public NavigationDrawerSubredditsAdapter(ArrayList<Subreddit> subreddits) {
            this.subreddits = subreddits;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = null;
            LayoutInflater layoutInflater = LayoutInflater.from(mainActivity);

            if (viewType == TYPE_HEADAER) {
                itemView = layoutInflater.inflate(R.layout.header_navigation_drawer_subreddits, parent, false);
                return new HeaderViewHolder(itemView);
            }
            else {
                itemView = layoutInflater.inflate(R.layout.row_navigation_drawer_subreddits, parent, false);
                return new SubredditViewHolder(itemView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SubredditViewHolder) {
                SubredditViewHolder subredditViewHolder = (SubredditViewHolder) holder;
                subredditViewHolder.subreddit.setText(subreddits.get(position - 1).getName());


                if (position == currentPos) {
                    subredditViewHolder.subreddit.setTextColor(res.getColor(R.color.accent));
                    subredditViewHolder.subreddit.setBackgroundColor(res.getColor(R.color.navigation_drawer_selected_item_background));
                }
                else {
                    subredditViewHolder.subreddit.setTextColor(res.getColor(R.color.text_primary));
                    subredditViewHolder.subreddit.setBackgroundColor(Color.TRANSPARENT);
                }
            }
            else if (holder instanceof HeaderViewHolder) {
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            }
        }

        @Override
        public int getItemCount() {
            return subreddits.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_HEADAER;
            }
            else {
                return TYPE_ITEM;
            }
        }

        /**
         * Updates index of selected item based on the current subreddit
         */
        public void resetSelectionIndex() {
            for (int i=0; i<subreddits.size(); i++) {
                if (subreddits.get(i).getName().equals(Globals.CURRENT_SUBREDDIT)) {
                    currentPos = i + 1;
                    oldPos = currentPos;
                }
            }
        }

        private class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private View addAccountContainer;
            private View accountInfoContainer;
            private TextView usernameTextView;
            private LinearLayout mainOptionsList;
            private TextView subredditsRecyclerViewHeadingTextView;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                addAccountContainer = itemView.findViewById(R.id.add_account_container);
                accountInfoContainer = itemView.findViewById(R.id.account_info_container);
                usernameTextView = (TextView) accountInfoContainer.findViewById(R.id.username_textview);
                mainOptionsList = (LinearLayout) itemView.findViewById(R.id.main_options_list);
                subredditsRecyclerViewHeadingTextView = (TextView) itemView.findViewById(R.id.subreddits_recycler_view_subheader_textview);

                addAccountContainer.setOnClickListener(this);
                accountInfoContainer.setOnClickListener(this);

                refreshNavigationDrawerHeader();

            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.add_account_container:
                        showAddAccountDialog();
                        break;
                    case R.id.account_info_container:
                        showExistingAccountsDialog();
                        break;
                    case R.id.main_option_container:
                        if (Globals.SESSION_COOKIE != null) {
                            switch (mainOptionsList.indexOfChild(v)) {
                                case 0:
                                    // Profile
                                    break;
                                case 1:
                                    // User
                                    break;
                                case 2:
                                    // Subreddut
                                    break;
                                case 3:
                                    // Settings
                                    break;
                                case 4:
                                    // Log out
                                    showLogoutConfirmationDialog();
                                    break;
                            }
                        }
                        else {
                            switch (mainOptionsList.indexOfChild(v)) {
                                case 0:
                                    // User
                                    break;
                                case 1:
                                    // Subreddut
                                    break;
                                case 2:
                                    // Settings
                                    break;
                            }
                        }
                }
            }

            /**
             * Should be invoked AFTER username has been written to GLOBAL shared preferences
             */
            private void refreshNavigationDrawerHeader() {
                // first set up top add account/account info container
                if (Helpers.getExistingAccounts(mainActivity).length > 0) {
                    if (Globals.SESSION_COOKIE != null) {
                        usernameTextView.setText(Helpers.getCurrentUsername(mainActivity));

                        subredditsRecyclerViewHeadingTextView.setText(R.string.navigation_drawer_subheader_subreddits_authenticated);
                    }
                    else {
                        usernameTextView.setText(R.string.navigation_drawer_login);

                        subredditsRecyclerViewHeadingTextView.setText(R.string.navigation_drawer_subheader_subreddits_default);
                    }

                    accountInfoContainer.setVisibility(View.VISIBLE);
                    addAccountContainer.setVisibility(View.INVISIBLE);
                }
                else {
                    subredditsRecyclerViewHeadingTextView.setText(R.string.navigation_drawer_subheader_subreddits_default);

                    accountInfoContainer.setVisibility(View.INVISIBLE);
                    addAccountContainer.setVisibility(View.VISIBLE);
                }

                // populate main options list
                setUpMainOptionsList();
            }

            private void setUpMainOptionsList() {
                String[] mainOptionsTitles;
                TypedArray mainOptionsIcons;

                LayoutInflater inflater = mainActivity.getLayoutInflater();

                if (Globals.SESSION_COOKIE != null) {
                    mainOptionsTitles = res.getStringArray(R.array.navigation_drawer_main_options_titles_authenticated);
                    mainOptionsIcons = res.obtainTypedArray(R.array.navigation_drawer_main_options_icons_authenticated);
                }
                else {
                    mainOptionsTitles = res.getStringArray(R.array.navigation_drawer_main_options_titles_anonymous);
                    mainOptionsIcons = res.obtainTypedArray(R.array.navigation_drawer_main_options_icons_anonymous);
                }

                // remove all child views before populating main options list
                mainOptionsList.removeAllViews();

                for (int i=0; i<mainOptionsTitles.length; i++) {
                    mainOptionsList.addView(getMainOptionView(mainOptionsTitles[i], mainOptionsIcons.getResourceId(i, -1), inflater));
                }
            }

            private View getMainOptionView(String title, int iconResId, LayoutInflater inflater) {
                View mainOptionView = inflater.inflate(R.layout.row_navigation_drawer_main_options, mainOptionsList, false);
                TextView titleTextView = (TextView) mainOptionView.findViewById(R.id.title_textview);
                ImageView iconImageView = (ImageView) mainOptionView.findViewById(R.id.icon_imageview);

                titleTextView.setText(title);
                iconImageView.setImageResource(iconResId);

                mainOptionView.setOnClickListener(this);

                return mainOptionView;
            }

            private void showAddAccountDialog() {
                // inflate custom dialog view
                View customView = mainActivity.getLayoutInflater().inflate(R.layout.dialog_add_account, null);
                final EditText usernameEditText = (EditText) customView.findViewById(R.id.username_edittext);
                final EditText passwordEditText = (EditText) customView.findViewById(R.id.password_edittext);

                final AlertDialog addAccountDialog = new AlertDialog.Builder(mainActivity)
                        .setView(customView)
                        .setPositiveButton(R.string.add_account_dialog_positive, null)  // set to null as onClick would be overridden later
                        .setNegativeButton(R.string.add_account_dialog_negative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setTitle(R.string.add_account_dialog_title)
                        .create();

                addAccountDialog.show();

                // override onClick of positive button as the default listener closes the dialog
                Button positiveButton = addAccountDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Helpers.hideKeyboard(mainActivity, usernameEditText.getWindowToken());

                        String username = usernameEditText.getText().toString().trim();
                        String password = passwordEditText.getText().toString().trim();

                        // validate user input
                        if (username.length() == 0) {
                            Toast.makeText(mainActivity, R.string.error_username_blank, Toast.LENGTH_SHORT).show();
                        } else if (password.length() == 0) {
                            Toast.makeText(mainActivity, R.string.error_password_blank, Toast.LENGTH_SHORT).show();
                        } else {
                            new AddAccountTask(username, password, addAccountDialog).execute();
                        }
                    }
                });
            }

            private void showExistingAccountsDialog() {
                final String[] existingAccounts = Helpers.getExistingAccounts(mainActivity);

                if (existingAccounts != null) {
                    final int addAccountItemIndex = existingAccounts.length - 1;
                    existingAccounts[addAccountItemIndex] = "Add Account";

                    // set default selection index to 'Add Account' item
                    int selectionIndex = addAccountItemIndex;

                    // get index of account with which the user is currently logged in
                    String currentUsername = Helpers.getCurrentUsername(mainActivity);
                    if (currentUsername != null) {
                        for (int i=0; i<existingAccounts.length; i++) {
                            if (existingAccounts[i].equals(currentUsername)) {
                                selectionIndex = i;
                            }
                        }
                    }

                    AlertDialog existingAccountsDialog = new AlertDialog.Builder(mainActivity)
                            .setSingleChoiceItems(existingAccounts, selectionIndex, null)
                            .setPositiveButton(R.string.choose_account_dialog_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();

                                    int selectionIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                    if (selectionIndex == addAccountItemIndex) {
                                        showAddAccountDialog();
                                    }
                                    else {
                                        String selectedUsername = existingAccounts[selectionIndex];

                                        // update last username in global preferences
                                        Helpers.writeToPreferences(mainActivity,
                                                Globals.GLOBAL_PREFS,
                                                Globals.GLOBAL_PREFS_LAST_USERNAME_KEY,
                                                selectedUsername);

                                        // update global SESSION_COOKIE
                                        Globals.SESSION_COOKIE = Helpers.readFromPreferences(
                                                mainActivity,
                                                Helpers.getUserPreferencesFileName(selectedUsername),
                                                Globals.USER_PREFS_SESSION_COOKIE_KEY);

                                        refreshNavigationDrawerAndPosts();

                                        // show login success message
                                        Toast.makeText(mainActivity, res.getString(R.string.success_login_base) + " " + selectedUsername, Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.choose_account_dialog_negative, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setTitle(R.string.choose_account_dialog_title)
                            .create();

                    existingAccountsDialog.show();
                }
                else {
                    Toast.makeText(mainActivity, R.string.error_no_existing_accounts, Toast.LENGTH_SHORT).show();
                }
            }

            private void showLogoutConfirmationDialog() {
                AlertDialog logoutConfirmationDialog = new AlertDialog.Builder(mainActivity)
                        .setTitle(R.string.logout_confirmation_dialog_title)
                        .setMessage(R.string.logout_confirmation_dialog_message)
                        .setPositiveButton(R.string.logout_confirmation_dialog_positive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // remove last username from global preferences
                                Helpers.writeToPreferences(
                                        mainActivity,
                                        Globals.GLOBAL_PREFS,
                                        Globals.GLOBAL_PREFS_LAST_USERNAME_KEY,
                                        null);

                                // remove global SESSION_COOKIE
                                Globals.SESSION_COOKIE = null;

                                refreshNavigationDrawerAndPosts();

                                // show logout success message
                                Toast.makeText(mainActivity, R.string.success_logout, Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(R.string.logout_confirmation_dialog_negative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create();
                logoutConfirmationDialog.show();
            }

            private void refreshNavigationDrawerAndPosts() {
                RedditApiWrapper.setSubredditDefaults();
                new GetSubredditsTask().execute();

                mainActivity.postsListFragment.refreshPosts();

                mainActivity.updateActionBarText();

                refreshNavigationDrawerHeader();
            }

            private class AddAccountTask extends AsyncTask<Void, Void, Boolean> {

                private final String TAG = AddAccountTask.class.getSimpleName();

                private String username;
                private String password;
                private AlertDialog addAccountDialog;
                private ProgressDialog progressDialog;

                public AddAccountTask(String username, String password, AlertDialog addAccountDialog) {
                    this.username = username;
                    this.password = password;
                    this.addAccountDialog = addAccountDialog;
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    progressDialog = new ProgressDialog(mainActivity);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage(res.getString(R.string.add_account_progress_dialog_message));

                    addAccountDialog.dismiss();
                    progressDialog.show();
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    return RedditApiWrapper.login(username, password);
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    super.onPostExecute(success);

                    progressDialog.cancel();

                    if (success) {
                        // save current user's session cookie and username in user preferences
                        String fileName = Helpers.getUserPreferencesFileName(username);
                        Helpers.writeToPreferences(mainActivity, fileName, Globals.USER_PREFS_USERNAME_KEY, username);
                        Helpers.writeToPreferences(mainActivity, fileName, Globals.USER_PREFS_SESSION_COOKIE_KEY, Globals.SESSION_COOKIE);

                        // update last username in global preferences
                        Helpers.writeToPreferences(mainActivity, Globals.GLOBAL_PREFS, Globals.GLOBAL_PREFS_LAST_USERNAME_KEY, username);

                        // add user account to list of existing accounts
                        Helpers.addAccountToExistingAccounts(mainActivity, username);

                        refreshNavigationDrawerAndPosts();

                        // show login success message
                        Toast.makeText(mainActivity, res.getString(R.string.success_login_base) + " " + username, Toast.LENGTH_LONG).show();
                    }
                    else {
                        addAccountDialog.show();
                        Toast.makeText(mainActivity, R.string.error_username_or_password_incorrect, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        private class SubredditViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView subreddit;

            public SubredditViewHolder(View itemView) {
                super(itemView);

                subreddit = (TextView) itemView.findViewById(R.id.subreddit);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                // highlight selected item and un-highlight previously selected item
                oldPos = currentPos;
                currentPos = this.getPosition();

                notifyItemChanged(oldPos);
                notifyItemChanged(currentPos);

                //close navigation drawer and refresh posts for the selected subreddit
                mainActivity.closeNavigationDrawer();
                Globals.CURRENT_SUBREDDIT = subreddit.getText().toString();
                mainActivity.postsListFragment.refreshPosts();
                mainActivity.updateActionBarText();
            }
        }
    }

    private class GetSubredditsTask extends AsyncTask<Void, Void, ArrayList<Subreddit>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            subredditsProgressBar.setVisibility(View.VISIBLE);
            subredditsRecyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected ArrayList<Subreddit> doInBackground(Void... params) {
            return RedditApiWrapper.getSubreddits(mainActivity);
        }

        @Override
        protected void onPostExecute(ArrayList<Subreddit> subreddits) {
            super.onPostExecute(subreddits);

            navigationDrawerSubredditsAdapter.subreddits.clear();
            navigationDrawerSubredditsAdapter.subreddits.addAll(subreddits);
            navigationDrawerSubredditsAdapter.subreddits.trimToSize();
            navigationDrawerSubredditsAdapter.resetSelectionIndex();
            navigationDrawerSubredditsAdapter.notifyDataSetChanged();

            subredditsProgressBar.setVisibility(View.INVISIBLE);
            subredditsRecyclerView.setVisibility(View.VISIBLE);

            mainActivity.closeNavigationDrawer();
        }
    }

}
