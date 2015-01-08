package com.android.redditreader.background_tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.redditreader.models.Subreddit;
import com.android.redditreader.utils.RedditApiWrapper;

import java.util.ArrayList;

public class GetSubredditsTask extends AsyncTask<Void, Void, ArrayList<Subreddit>> {

    private Context context;
    private TaskCallbacks taskCallbacks;

    public GetSubredditsTask(Context context, TaskCallbacks taskCallbacks) {
        this.context = context;
        this.taskCallbacks = taskCallbacks;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (taskCallbacks != null) {
            taskCallbacks.onPreExecute();
        }
    }

    @Override
    protected ArrayList<Subreddit> doInBackground(Void... params) {
        return RedditApiWrapper.getSubreddits(context);
    }

    @Override
    protected void onPostExecute(ArrayList<Subreddit> subreddits) {
        super.onPostExecute(subreddits);

        if (taskCallbacks != null) {
            taskCallbacks.onPostExecute(subreddits);
        }
    }

    public static interface TaskCallbacks {
        public void onPreExecute();

        public void onPostExecute(ArrayList<Subreddit> subreddits);
    }
}
