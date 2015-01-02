package com.android.redditreader.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
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

        // set up subreddits recycler view
        navigationDrawerSubredditsAdapter = new NavigationDrawerSubredditsAdapter(new ArrayList<Subreddit>());
        subredditsRecyclerView.setAdapter(navigationDrawerSubredditsAdapter);
        subredditsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        refreshSubreddits();
    }

    private void findViews(View view) {
        subredditsRecyclerView = (RecyclerView) view.findViewById(R.id.subreddits_recycler_view);
        subredditsProgressBar = (ProgressBar) view.findViewById(R.id.subreddits_progressbar);
    }

    private void refreshSubreddits() {
        new GetSubredditsTask().execute();
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

                Resources res = mainActivity.getResources();
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

        private class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private View addAccountContainer;
            private View accountInfoContainer;
            private TextView usernameTextView;
            private View viewUserProfileContainer;
            private View manageSubredditsContainer;
            private View viewSettingsContainer;
            private TextView subredditsRecyclerViewHeadingTextView;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                addAccountContainer = itemView.findViewById(R.id.add_account_container);
                accountInfoContainer = itemView.findViewById(R.id.account_info_container);
                usernameTextView = (TextView) accountInfoContainer.findViewById(R.id.username_textview);
                viewUserProfileContainer = itemView.findViewById(R.id.view_user_profile_container);
                manageSubredditsContainer = itemView.findViewById(R.id.manage_subreddits_container);
                viewSettingsContainer = itemView.findViewById(R.id.view_setttings_container);
                subredditsRecyclerViewHeadingTextView = (TextView) itemView.findViewById(R.id.subreddits_recycler_view_subheader_textview);

                addAccountContainer.setOnClickListener(this);

                refreshNavigationDrawerHeader();
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.add_account_container:
                        showAddAccountDialog();
                        break;
                }
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
                    progressDialog.setMessage("Adding account...");

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
                        mainActivity.closeNavigationDrawer();

                        refreshSubreddits();

                        mainActivity.postsListFragment.refreshPosts();

                        // save current user's session cookie and username in user preferences
                        String fileName = Helpers.getUserPreferencesFileName(username);
                        Helpers.writeToPreferences(mainActivity, fileName, Globals.USER_PREFS_USERNAME_KEY, username);
                        Helpers.writeToPreferences(mainActivity, fileName, Globals.USER_PREFS_SESSION_COOKIE_KEY, Globals.SESSION_COOKIE);

                        // update latest username in global preferences
                        Helpers.writeToPreferences(mainActivity, Globals.GLOBAL_PREFS, Globals.GLOBAL_PREFS_LAST_USERNAME_KEY, username);

                        refreshNavigationDrawerHeader();

                        String successMessageBase = getResources().getString(R.string.success_login_base);
                        Toast.makeText(mainActivity, successMessageBase + " " + username, Toast.LENGTH_LONG).show();
                    }
                    else {
                        addAccountDialog.show();
                        Toast.makeText(mainActivity, R.string.error_username_or_password_incorrect, Toast.LENGTH_LONG).show();
                    }
                }
            }

            /**
             * Should be invoked AFTER username has been written to GLOBAL shared preferences
             */
            private void refreshNavigationDrawerHeader() {
                if (Globals.SESSION_COOKIE != null) {
                    usernameTextView.setText(Helpers.readFromPreferences(mainActivity, Globals.GLOBAL_PREFS,
                            Globals.GLOBAL_PREFS_LAST_USERNAME_KEY));

                    subredditsRecyclerViewHeadingTextView.setText(R.string.navigation_drawer_subheader_subreddits_authenticated);

                    accountInfoContainer.setVisibility(View.VISIBLE);
                    addAccountContainer.setVisibility(View.INVISIBLE);

                }
                else {
                    subredditsRecyclerViewHeadingTextView.setText(R.string.navigation_drawer_subheader_subreddits_default);

                    accountInfoContainer.setVisibility(View.INVISIBLE);
                    addAccountContainer.setVisibility(View.VISIBLE);
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
            navigationDrawerSubredditsAdapter.notifyDataSetChanged();

            subredditsProgressBar.setVisibility(View.INVISIBLE);
            subredditsRecyclerView.setVisibility(View.VISIBLE);

        }
    }

}
