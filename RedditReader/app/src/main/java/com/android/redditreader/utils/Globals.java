package com.android.redditreader.utils;

/**
 * Created by Manas on 29-12-2014.
 */
public class Globals {

    public static final String USER_AGENT = "RedditReader by enthusiast_94";

    // urls
    public static final String API_BASE_URL = "http://www.reddit.com";
    public static final String API_LOGIN_URL = API_BASE_URL + "/api/login";
    public static final String API_VOTE_URL = API_BASE_URL + "/api/vote";

    // shared preferences file and key names
    public static final String GLOBAL_PREFS = "global_prefs";
    public static final String GLOBAL_PREFS_LAST_USERNAME_KEY = "last_username";
    public static final String GLOBAl_PREFS_EXISTING_ACCOUNTS = "existing_accounts";
    public static final String USER_PREFS_SESSION_COOKIE_KEY = "session_key";
    public static final String USER_PREFS_USERNAME_KEY = "username";
    public static final String USER_PREFS_LAST_SUBREDDIT = "last_subreddit";
    public static final String USER_PREFS_LAST_SORT = "last_sort";
    public static final String USER_PREFS_LAST_TIME = "last_time";

    public static final String DEFAULT_SUBREDDIT = "Front Page";
    public static final String DEFAULT_SORT = "Hot";
    public static final int DEFAULT_SUBREDDITS_LIMIT = 100;

    public static String CURRENT_SUBREDDIT = DEFAULT_SUBREDDIT;
    public static String CURRENT_SORT = DEFAULT_SORT;
    public static String CURRENT_TIME = null;
    public static String CURRENT_POSTS_AFTER = null;
    public static String CURRENT_SUBREDDITS_AFTER = null;

    public static String SESSION_COOKIE = null;
    public static String MODHASH = null;  // A modhash is a token that the reddit API requires to help prevent CSRF


}
