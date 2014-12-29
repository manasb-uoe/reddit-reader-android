package com.android.redditreader.background_tasks;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.android.redditreader.adapters.PostsAdapter;
import com.android.redditreader.models.Post;
import com.android.redditreader.utils.DataParser;

import java.net.URL;
import java.util.ArrayList;

/**
* Created by Manas on 29-12-2014.
*/
public class GetPostsTask extends AsyncTask<Void, Void, ArrayList<Post>> {

    private final String TAG = GetPostsTask.class.getSimpleName();
    private URL url;
    private PostsAdapter postsAdapter;
    private ProgressBar progressBar;
    private RecyclerView postsRecyclerView;

    public GetPostsTask(URL url, PostsAdapter postsAdapter, RecyclerView postsRecyclerView, ProgressBar postsProgressBar) {
        this.url = url;
        this.postsAdapter = postsAdapter;
        this.progressBar = postsProgressBar;
        this.postsRecyclerView = postsRecyclerView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (postsRecyclerView != null) {
            postsRecyclerView.setVisibility(View.GONE);
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected ArrayList<Post> doInBackground(Void... params) {
        return DataParser.parsePosts(url);
    }

    @Override
    protected void onPostExecute(ArrayList<Post> posts) {
        super.onPostExecute(posts);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (postsRecyclerView != null) {
            postsRecyclerView.setVisibility(View.VISIBLE);
        }

        postsAdapter.posts.clear();
        postsAdapter.posts.addAll(posts);
        postsAdapter.posts.trimToSize();
        postsAdapter.notifyDataSetChanged();
    }
}

