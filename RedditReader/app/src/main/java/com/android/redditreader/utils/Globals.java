package com.android.redditreader.utils;

/**
 * Created by Manas on 29-12-2014.
 */
public class Globals {

    // urls
    public static final String BASE_API_URL = "http://www.reddit.com";
    public static final String REDDIT_LOGIN_URL = "https://ssl.reddit.com/api/login";
    public static final String THUMBNAIL_SELF = "http://i.imgur.com/7TqyJpu.png";
    public static final String THUMBNAIL_NSFW = "http://i.imgur.com/9YCjKOr.png";

    // shared preferences file and key names
    public static final String GLOBAL_PREFS = "global_prefs";
    public static final String GLOBAL_PREFS_LAST_USERNAME_KEY = "last_username";
    public static final String GLOBAl_PREFS_EXISTING_ACCOUNTS = "existing_accounts";
    public static final String USER_PREFS_SESSION_COOKIE_KEY = "session_key";
    public static final String USER_PREFS_USERNAME_KEY = "username";

    public static final String DEFAULT_SUBREDDIT = "Front Page";
    public static final String DEFAULT_SORT = "Hot";
    public static final int DEFAULT_SUBREDDITS_LIMIT = 100;

    public static String CURRENT_SUBREDDIT = DEFAULT_SUBREDDIT;
    public static String CURRENT_SORT = DEFAULT_SORT;
    public static String CURRENT_TIME = null;
    public static String CURRENT_POSTS_AFTER = null;
    public static String CURRENT_SUBREDDITS_AFTER = null;

    public static String SESSION_COOKIE = null;


}
