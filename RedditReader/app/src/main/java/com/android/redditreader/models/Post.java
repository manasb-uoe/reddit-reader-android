package com.android.redditreader.models;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Manas on 29-12-2014.
 */
public class Post {

    private String domain;
    private String subreddit;
    private String author;
    private int score;
    private boolean nsfw;
    private String thumbnail;
    private String created;
    private String title;
    private String url;
    private int num_comments;
    private String permalink;
    private String selftext;

    public Post() {
        // empty constructor
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        Date createdAt = new Date(Double.valueOf(created).longValue() * 1000);
        Date now = new Date();
        long elapsed = now.getTime() - createdAt.getTime();

        long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(elapsed);
        long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(elapsed);
        long diffHours = TimeUnit.MILLISECONDS.toHours(elapsed);
        long diffDays = TimeUnit.MILLISECONDS.toDays(elapsed);
        long diffWeeks = diffDays / 7;

        if (diffWeeks > 0) {
            this.created = diffWeeks == 1 ? diffWeeks + " week ago" : diffWeeks + " weeks ago";
        }
        else if (diffDays > 0) {
            this.created = diffDays == 1 ? diffDays + " day ago" : diffDays + " days ago";
        }
        else if (diffHours > 0) {
            this.created = diffHours == 1 ? diffHours + " hour ago" : diffHours + " hours ago";
        }
        else if (diffMinutes > 0) {
            this.created = diffMinutes == 1 ? diffMinutes + " minute ago" : diffMinutes + " minutes ago";
        }
        else {
            this.created = diffSeconds == 1 ? diffSeconds + " second ago" : diffSeconds + " seconds ago";
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNum_comments() {
        return num_comments;
    }

    public void setNum_comments(int num_comments) {
        this.num_comments = num_comments;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getSelftext() {
        return selftext;
    }

    public void setSelftext(String selftext) {
        this.selftext = selftext;
    }
}

