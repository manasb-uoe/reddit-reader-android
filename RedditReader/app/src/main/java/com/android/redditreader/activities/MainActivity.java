package com.android.redditreader.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.redditreader.R;
import com.android.redditreader.fragments.PostsListFragment;
import com.android.redditreader.utils.Globals;


public class MainActivity extends ActionBarActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private PostsListFragment postsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        postsListFragment = (PostsListFragment) getSupportFragmentManager().findFragmentById(R.id.posts_list_fragment);

        // enable UP button so that it can be used by ActionBarDrawerToggle
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        updateActionBarText();

        setUpNavigationDrawer();
    }

    private void findViews() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
    }

    private void setUpNavigationDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_navigation_drawer, R.string.close_navigation_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                actionBar.setTitle(getResources().getString(R.string.app_name));
                actionBar.setSubtitle("");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                updateActionBarText();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void updateActionBarText() {
        if (!Globals.CURRENT_SUBREDDIT.equals(Globals.DEFAULT_SUBREDDIT)) {
            actionBar.setTitle("/r/" + Globals.CURRENT_SUBREDDIT);
        }
        else {
            actionBar.setTitle(Globals.CURRENT_SUBREDDIT);
        }

        if (Globals.CURRENT_TIME != null) {
            actionBar.setSubtitle(Globals.CURRENT_SORT + ": " + Globals.CURRENT_TIME);
        }
        else {
            actionBar.setSubtitle(Globals.CURRENT_SORT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (id == R.id.action_sort_hot || id == R.id.action_sort_new || id == R.id.action_sort_rising) {
            Globals.CURRENT_SORT = item.getTitle().toString();
            Globals.CURRENT_TIME = null;

            updateActionBarText();

            postsListFragment.refreshPosts();
        }
        if (id == R.id.action_sort_top_hour || id == R.id.action_sort_top_day || id == R.id.action_sort_top_week
                || id == R.id.action_sort_top_month || id == R.id.action_sort_top_year || id == R.id.action_sort_top_all) {
            Globals.CURRENT_SORT = "Top";
            Globals.CURRENT_TIME = item.getTitle().toString();

            updateActionBarText();

            postsListFragment.refreshPosts();
        }
        if (id == R.id.action_sort_controversial_hour || id == R.id.action_sort_controversial_day
                || id == R.id.action_sort_controversial_week || id == R.id.action_sort_controversial_month ||
                id == R.id.action_sort_controversial_year || id == R.id.action_sort_controversial_all) {
            Globals.CURRENT_SORT = "Controversial";
            Globals.CURRENT_TIME = item.getTitle().toString();

            updateActionBarText();

            postsListFragment.refreshPosts();
        }

        return super.onOptionsItemSelected(item);
    }
}
