package com.android.redditreader.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.redditreader.R;
import com.android.redditreader.adapters.FavouriteSubredditsAdapter;
import com.android.redditreader.background_tasks.GetSubredditsTask;
import com.android.redditreader.models.Subreddit;

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
        favouriteSubredditsAdapter = new FavouriteSubredditsAdapter(this, subreddits);
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
}
