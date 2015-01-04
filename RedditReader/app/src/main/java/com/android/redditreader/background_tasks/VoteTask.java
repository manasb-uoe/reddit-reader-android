package com.android.redditreader.background_tasks;

import android.os.AsyncTask;

import com.android.redditreader.utils.RedditApiWrapper;

public class VoteTask extends AsyncTask<Void, Void, Boolean> {

    private String fullName;
    private int voteDirection;

    public VoteTask(String fullName, int voteDirection) {
        this.fullName = fullName;
        this.voteDirection = voteDirection;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return RedditApiWrapper.vote(fullName, voteDirection);
    }

}
