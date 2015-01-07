package com.android.redditreader.models;

import java.text.NumberFormat;

/**
 * Created by Manas on 02-01-2015.
 */
public class Subreddit {

    private String name;
    private String numOfSubscribers;
    private boolean isSubscribed;
    private String description;

    public Subreddit() {
        //empty constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumOfSubscribers() {
        return numOfSubscribers;
    }

    public void setNumOfSubscribers(String numOfSubscribers) {
        this.numOfSubscribers = NumberFormat.getInstance().format(Long.valueOf(numOfSubscribers));
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean isSubscribed) {
        this.isSubscribed = isSubscribed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
