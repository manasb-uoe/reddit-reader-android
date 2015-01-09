package com.android.redditreader.background_tasks;

import android.os.AsyncTask;

import com.android.redditreader.utils.RedditApiWrapper;

public class SubscribeTask extends AsyncTask<Void, Void, Boolean> {

    private String fullName;
    private boolean shouldSub;
    private TaskCallbacks taskCallbacks;

    public SubscribeTask(String fullName, boolean shouldSub, TaskCallbacks taskCallbacks) {
        this.fullName = fullName;
        this.shouldSub = shouldSub;
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
    protected Boolean doInBackground(Void... params) {
        return RedditApiWrapper.subscribe(fullName, shouldSub);
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);

        if (taskCallbacks != null) {
            taskCallbacks.onPostExecute(success);
        }
    }

    public static interface TaskCallbacks {
        public void onPreExecute();

        public void onPostExecute(boolean success);
    }
}
