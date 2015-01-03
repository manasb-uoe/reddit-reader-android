package com.android.redditreader.utils;

import android.content.Context;
import android.util.Log;

import com.android.redditreader.models.Post;
import com.android.redditreader.models.Subreddit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Manas on 29-12-2014.
 */
public class RedditApiWrapper {

    private static final String TAG = RedditApiWrapper.class.getSimpleName();

    public static boolean login(String username, String password) {
        HttpURLConnection conn = null;

        try {
            conn = Helpers.getConnection(new URL(Globals.REDDIT_LOGIN_URL), "POST");
            String postData = "user=" + username + "&passwd=" + password + "&rem=True";
            Helpers.writeToConnection(conn, postData);
            String cookie = conn.getHeaderField("set-cookie");

            if (cookie != null) {
                cookie = cookie.split(";")[0];
                if (cookie.startsWith("reddit_session")) {
                    Globals.SESSION_COOKIE = cookie;
                    Log.e(TAG, Globals.SESSION_COOKIE);
                    return true;
                }
            }
        }
        catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return false;
    }

    public static void setSubredditDefaults() {
        Globals.CURRENT_SUBREDDIT = Globals.DEFAULT_SUBREDDIT;
        Globals.CURRENT_SORT = Globals.DEFAULT_SORT;
        Globals.CURRENT_TIME = null;
    }

    public static ArrayList<Post> getPosts(URL url) {
        ArrayList<Post> posts = null;

        String content = Helpers.readStringFromConnection(Helpers.getConnection(url, "GET"));

        if (content != null) {
            try {
                JSONObject main = new JSONObject(content);
                JSONObject data = (JSONObject) main.get("data");
                JSONArray children = (JSONArray) data.get("children");

                posts = new ArrayList<Post>();

//                TODO get after id
//                Utility.POSTS_AFTER = (String) data.get("after");

                //iterate through the array of posts
                for (int i=0;i<children.length();i++) {
                    JSONObject post_num = (JSONObject) children.get(i);
                    JSONObject data2 = (JSONObject) post_num.get("data");

                    //get the required post data
                    Post post = new Post();
                    post.setDomain(data2.get("domain").toString());
                    post.setSubreddit(data2.get("subreddit").toString());
                    post.setAuthor(data2.get("author").toString());
                    post.setScore(Integer.parseInt(data2.get("score").toString()));
                    post.setCreated(data2.get("created_utc").toString());
                    post.setNsfw(data2.getBoolean("over_18"));
                    post.setThumbnail(data2.get("thumbnail").toString());
                    post.setUrl(data2.get("url").toString());
                    post.setTitle(data2.get("title").toString());
                    post.setNum_comments(Integer.parseInt(data2.get("num_comments").toString()));
                    post.setPermalink(data2.get("permalink").toString());
                    post.setSelftext(data2.get("selftext").toString());

                    //add post to list of posts
                    posts.add(post);
                }
            }
            catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return posts;
    }

    public static ArrayList<Subreddit> getSubreddits(Context context) {
        ArrayList<Subreddit> subreddits = new ArrayList<>();

        if (Globals.SESSION_COOKIE != null) {
            String content = Helpers.readStringFromConnection(Helpers.getConnection(Helpers.getUserSubredditsURL(), "GET"));
            subreddits = parseSubreddits(content, subreddits);
            while (Globals.CURRENT_SUBREDDITS_AFTER != null) {
                content = Helpers.readStringFromConnection(Helpers.getConnection(Helpers.getUserSubredditsURL(), "GET"));
                subreddits = parseSubreddits(content, subreddits);
            }
        }
        else {
            subreddits = Helpers.getDefaultSubredditsFromAsset(context);
        }

        // add front page as the first subreddit
        Subreddit frontPage = new Subreddit();
        frontPage.setName("Front Page");
        subreddits.add(0, frontPage);

        Log.e(TAG, subreddits.size()+"");
        return subreddits;
    }

    private static ArrayList<Subreddit> parseSubreddits(String content, ArrayList<Subreddit> subreddits) {
        try {
            JSONObject main = new JSONObject(content);
            JSONObject data = (JSONObject) main.get("data");
            JSONArray children = (JSONArray) data.get("children");

            if (data.isNull("after")) {
                Globals.CURRENT_SUBREDDITS_AFTER = null;
            }
            else {
                Globals.CURRENT_SUBREDDITS_AFTER = data.getString("after");
            }

            for (int i=0;i<children.length();i++) {
                JSONObject subreddit_num = (JSONObject) children.get(i);
                JSONObject data2 = (JSONObject) subreddit_num.get("data");

                Subreddit subreddit = new Subreddit();
                subreddit.setName(data2.getString("display_name"));
                subreddit.setNumOfSubscribers(data2.getLong("subscribers"));
                subreddit.setDescription(data2.getString("description_html"));
                subreddit.setSubscribed(data2.getBoolean("user_is_subscriber"));
                subreddits.add(subreddit);
            }
        }
        catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            subreddits = null;
        }

        return subreddits;
    }
}
