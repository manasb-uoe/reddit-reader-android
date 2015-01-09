package com.android.redditreader.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.redditreader.R;
import com.android.redditreader.background_tasks.GetSubredditsTask;
import com.android.redditreader.background_tasks.SubscribeTask;
import com.android.redditreader.models.Subreddit;
import com.android.redditreader.utils.Globals;

import java.util.ArrayList;

public class FavouriteSubredditsActivity extends ActionBarActivity {

    private static final String TAG = FavouriteSubredditsActivity.class.getSimpleName();

    private RecyclerView subredditsRecyclerView;
    private FavouriteSubredditsAdapter favouriteSubredditsAdapter;
    private ProgressBar subredditsProgressBar;
    private ArrayList<Subreddit> subreddits;

    public static final String FAVOURITE_SUBREDDITS_RESULT_KEY = "favourite_subreddits";
    private static final String RETAINED_SUBREDDITS_KEY = "retained_subreddits";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_subreddits);

        findViews();

        if (savedInstanceState == null) {
            subreddits = new ArrayList<>();
            setUpSubreddits();
            refreshSubreddits();
        }
        else {
            subreddits = savedInstanceState.getParcelableArrayList(RETAINED_SUBREDDITS_KEY);
            setUpSubreddits();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RETAINED_SUBREDDITS_KEY, favouriteSubredditsAdapter.subreddits);
    }

    private void findViews() {
        subredditsRecyclerView = (RecyclerView) findViewById(R.id.subreddits_recycler_view);
        subredditsProgressBar = (ProgressBar) findViewById(R.id.subreddits_progressbar);
    }

    private void setUpSubreddits() {
        favouriteSubredditsAdapter = new FavouriteSubredditsAdapter(subreddits);
        subredditsRecyclerView.setAdapter(favouriteSubredditsAdapter);
        subredditsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void refreshSubreddits() {
        new GetSubredditsTask(
                this,
                new GetSubredditsTask.TaskCallbacks() {
                    @Override
                    public void onPreExecute() {
                        subredditsProgressBar.setVisibility(View.VISIBLE);
                        subredditsRecyclerView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onPostExecute(ArrayList<Subreddit> subreddits) {
                        favouriteSubredditsAdapter.updateDataSetAndNotifyChanges(subreddits);

                        subredditsProgressBar.setVisibility(View.INVISIBLE);
                        subredditsRecyclerView.setVisibility(View.VISIBLE);
                    }
                })
                .execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subreddits, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            setActivityResultData();
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setActivityResultData() {
        Intent data = new Intent();
        data.putParcelableArrayListExtra(FAVOURITE_SUBREDDITS_RESULT_KEY, favouriteSubredditsAdapter.getFavouriteSubreddits());
        setResult(Activity.RESULT_OK, data);
    }

    @Override
    public void onBackPressed() {
        setActivityResultData();
        super.onBackPressed();
    }

    private class FavouriteSubredditsAdapter extends RecyclerView.Adapter<FavouriteSubredditsAdapter.SubredditViewHolder> {

        private final String TAG = FavouriteSubredditsAdapter.class.getSimpleName();

        public ArrayList<Subreddit> subreddits;

        private Resources res;
        private int accentColor;
        private int textPrimaryColor;
        private Typeface normalTypeFace;
        private Typeface boldTypeFace;
        private String favouriteAddedMessageBase;
        private String favouriteRemovedMessageBase;

        public FavouriteSubredditsAdapter(ArrayList<Subreddit> subreddits) {
            this.subreddits = subreddits;

            this.res = FavouriteSubredditsActivity.this.getResources();
            accentColor = res.getColor(R.color.accent);
            textPrimaryColor = res.getColor(R.color.text_primary);
            normalTypeFace = Typeface.create("sans-serif", Typeface.NORMAL);
            boldTypeFace = Typeface.create("sans-serif", Typeface.BOLD);
            favouriteAddedMessageBase = res.getString(R.string.success_added_to_favourites_base);
            favouriteRemovedMessageBase = res.getString(R.string.success_removed_from_favourites_base);
        }

        @Override
        public SubredditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(FavouriteSubredditsActivity.this).inflate(R.layout.row_favourite_subreddits, parent, false);
            return new SubredditViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(SubredditViewHolder holder, int position) {
            Subreddit subreddit = subreddits.get(position);

            holder.subredditTextView.setText(subreddit.getName());
            if (!subreddit.getName().equals(Globals.DEFAULT_SUBREDDIT)) {
                holder.numSubscribersTextView.setVisibility(View.VISIBLE);
                holder.numSubscribersTextView.setText(subreddit.getNumOfSubscribers() + " subscribers");
            }
            else {
                holder.numSubscribersTextView.setVisibility(View.GONE);
            }

            if (subreddit.isFavourite()) {
                holder.setChecked(true);
            }
            else {
                holder.setChecked(false);
            }
        }

        @Override
        public int getItemCount() {
            return subreddits.size();
        }

        public void updateDataSetAndNotifyChanges(ArrayList<Subreddit> subreddits) {
            this.subreddits.clear();
            this.subreddits.addAll(subreddits);
            this.subreddits.trimToSize();
            notifyDataSetChanged();
        }

        public ArrayList<Subreddit> getFavouriteSubreddits() {
            ArrayList<Subreddit> favouriteSubreddits = new ArrayList<>();

            for (Subreddit subreddit : subreddits) {
                if (subreddit.isFavourite()) {
                    favouriteSubreddits.add(subreddit);
                }
            }

            return favouriteSubreddits;
        }

        public class SubredditViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            private TextView subredditTextView;
            private TextView numSubscribersTextView;
            private CheckBox favouriteCheckBox;

            public SubredditViewHolder(View itemView) {
                super(itemView);

                subredditTextView = (TextView) itemView.findViewById(R.id.subreddit_textview);
                numSubscribersTextView = (TextView) itemView.findViewById(R.id.num_subscribers_textview);
                favouriteCheckBox = (CheckBox) itemView.findViewById(R.id.favourite_checkbox);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Subreddit selectedSubreddit = subreddits.get(getPosition());

                switch (v.getId()) {
                    case R.id.subreddit_container:
                        if (!selectedSubreddit.getName().equals(Globals.DEFAULT_SUBREDDIT)) {
                            if (selectedSubreddit.isFavourite()) {
                                selectedSubreddit.setFavourite(false);
                                setChecked(false);
                                Toast.makeText(FavouriteSubredditsActivity.this, subredditTextView.getText() + " " + favouriteRemovedMessageBase, Toast.LENGTH_SHORT)
                                        .show();
                            }
                            else {
                                selectedSubreddit.setFavourite(true);
                                setChecked(true);
                                Toast.makeText(FavouriteSubredditsActivity.this, subredditTextView.getText() + " " + favouriteAddedMessageBase, Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                        else {
                            Toast.makeText(FavouriteSubredditsActivity.this, R.string.error_cannot_remove_front_page_from_favourites, Toast.LENGTH_SHORT)
                                    .show();
                        }
                        break;
                }
            }

            @Override
            public boolean onLongClick(View v) {
                Subreddit selectedSubreddit = subreddits.get(getPosition());

                switch (v.getId()) {
                    case R.id.subreddit_container:
                        showSubredditOptionsDialog(selectedSubreddit);
                        return true;
                    default:
                        return false;
                }
            }

            private void showSubredditOptionsDialog(final Subreddit selectedSubreddit) {
                AlertDialog subredditOptionsDialog = new AlertDialog.Builder(FavouriteSubredditsActivity.this)
                        .setItems(R.array.subreddit_options_dialog, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        // TODO new favourites disappear after unsubscribing
                                        // Unsubscribe
                                        new SubscribeTask(
                                                selectedSubreddit.getFullName(),
                                                false,
                                                new SubscribeTask.TaskCallbacks() {
                                                    ProgressDialog progressDialog;

                                                    @Override
                                                    public void onPreExecute() {
                                                        progressDialog = new ProgressDialog(FavouriteSubredditsActivity.this);
                                                        progressDialog.setMessage(res.getString(R.string.please_wait_progress_dialog_message));
                                                        progressDialog.show();
                                                    }

                                                    @Override
                                                    public void onPostExecute(boolean success) {
                                                        progressDialog.cancel();

                                                        String toastMessage;
                                                        if (success) {
                                                            toastMessage = res.getString(R.string.subreddit_unsubscribe_base) + " " + selectedSubreddit.getName();
                                                            refreshSubreddits();
                                                        }
                                                        else {
                                                            toastMessage = res.getString(R.string.error_failed_to_perform_requested_operation);
                                                        }
                                                        Toast.makeText(FavouriteSubredditsActivity.this, toastMessage, Toast.LENGTH_SHORT)
                                                                .show();
                                                    }
                                                })
                                                .execute();
                                        break;
                                    case 1:
                                        // View Description
                                        break;
                                }
                            }
                        })
                        .create();

                subredditOptionsDialog.show();
            }

            private void setChecked(boolean checked) {
                if (checked) {
                    favouriteCheckBox.setChecked(true);
                    subredditTextView.setTypeface(boldTypeFace);
                    subredditTextView.setTextColor(accentColor);
                }
                else {
                    favouriteCheckBox.setChecked(false);
                    subredditTextView.setTypeface(normalTypeFace);
                    subredditTextView.setTextColor(textPrimaryColor);
                }
            }
        }
    }

}
