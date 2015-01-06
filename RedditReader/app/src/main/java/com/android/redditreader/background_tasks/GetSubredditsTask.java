package com.android.redditreader.background_tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.redditreader.models.Subreddit;
import com.android.redditreader.utils.RedditApiWrapper;

import java.util.ArrayList;

public class GetSubredditsTask extends AsyncTask<Void, Void, ArrayList<Subreddit>> {

    private Context context;
    private PreExecuteCallback preExecuteCallback;
    private PostExecuteCallback postExecuteCallback;

    public GetSubredditsTask(Context context, PreExecuteCallback preExecuteCallback, PostExecuteCallback postExecuteCallback) {
        this.context = context;
        this.preExecuteCallback = preExecuteCallback;
        this.postExecuteCallback = postExecuteCallback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (preExecuteCallback != null) {
            preExecuteCallback.onPreExecute();
        }
    }

    @Override
    protected ArrayList<Subreddit> doInBackground(Void... params) {
        return RedditApiWrapper.getSubreddits(context);
    }

    @Override
    protected void onPostExecute(ArrayList<Subreddit> subreddits) {
        super.onPostExecute(subreddits);

        if (postExecuteCallback != null) {
            postExecuteCallback.onPostExecute(subreddits);
        }
    }

    public static interface PostExecuteCallback {
        public void onPostExecute(ArrayList<Subreddit> subreddits);
    }

    public static interface PreExecuteCallback {
        public void onPreExecute();
    }
}
