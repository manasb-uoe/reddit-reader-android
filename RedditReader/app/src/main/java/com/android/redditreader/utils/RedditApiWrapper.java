package com.android.redditreader.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.redditreader.models.Post;
import com.android.redditreader.models.Subreddit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Manas on 29-12-2014.
 */
public class RedditApiWrapper {

    private static final String TAG = RedditApiWrapper.class.getSimpleName();

    public static URL getCurrentSubredditURL() {
        String base = Globals.API_BASE_URL;

        if (!Globals.CURRENT_SUBREDDIT.equals(Globals.DEFAULT_SUBREDDIT)) {
            base = base + "/r/" + Globals.CURRENT_SUBREDDIT + "/" + Globals.CURRENT_SORT.toLowerCase() + "/.json";
        } else {
            base = base + "/" + Globals.CURRENT_SORT.toLowerCase() + "/.json";
        }

        URL builtURL;
        try {
            Uri.Builder builder = Uri.parse(base).buildUpon();
            builder = Globals.CURRENT_TIME != null ? builder.appendQueryParameter("t", Globals.CURRENT_TIME.toLowerCase()) : builder;
//            builder = builder.appendQueryParameter("limit", String.valueOf(Globals.DEFAULT_LIMIT));
            builder = Globals.CURRENT_POSTS_AFTER != null ? builder.appendQueryParameter("after", Globals.CURRENT_POSTS_AFTER) : builder;
            builtURL = new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            builtURL = null;
            Log.e(TAG, e.getMessage());
        }

        return builtURL;
    }

    public static URL getUserSubredditsURL() {
        String base = Globals.API_BASE_URL + "/reddits/mine.json";

        URL builtURL;
        try {
            Uri.Builder builder = Uri.parse(base).buildUpon();
            builder = builder.appendQueryParameter("limit", String.valueOf(Globals.DEFAULT_SUBREDDITS_LIMIT));
            builder = Globals.CURRENT_SUBREDDITS_AFTER != null ? builder.appendQueryParameter("after", Globals.CURRENT_SUBREDDITS_AFTER) : builder;
            builtURL = new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            builtURL = null;
            Log.e(TAG, e.getMessage());
        }

        return builtURL;
    }

    public static boolean login(String username, String password) {
        HttpURLConnection conn = null;

        try {
            conn = Helpers.getConnection(new URL(Globals.API_LOGIN_URL), "POST", false);
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

        String content = Helpers.readStringFromConnection(Helpers.getConnection(url, "GET", true));

        if (content != null) {
            try {
                JSONObject main = new JSONObject(content);
                JSONObject data = (JSONObject) main.get("data");
                JSONArray children = (JSONArray) data.get("children");

                posts = new ArrayList<Post>();

//                TODO get after id
//                Utility.POSTS_AFTER = (String) data.get("after");

                // update modhash
                Globals.MODHASH = !data.isNull("modhash") ? data.getString("modhash") : null;

                for (int i=0;i<children.length();i++) {
                    JSONObject post_num = (JSONObject) children.get(i);
                    JSONObject data2 = (JSONObject) post_num.get("data");

                    Post post = new Post();
                    post.setFullName(data2.getString("name"));
                    post.setLiked(!data2.isNull("likes") ? data2.getBoolean("likes") : null);
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
            String content = Helpers.readStringFromConnection(Helpers.getConnection(getUserSubredditsURL(), "GET", true));
            subreddits = parseSubreddits(content, subreddits);
            while (Globals.CURRENT_SUBREDDITS_AFTER != null) {
                content = Helpers.readStringFromConnection(Helpers.getConnection(getUserSubredditsURL(), "GET", true));
                subreddits = parseSubreddits(content, subreddits);
            }

            // set favourite subreddits using the array of favourite subreddit names for current user
            String[] favouriteSubredditNames = Helpers.getFavouriteSubredditsForCurrentUser(context);
            if (favouriteSubredditNames != null) {
                for (String favouriteSubredditName : favouriteSubredditNames) {
                    for (Subreddit subreddit : subreddits) {
                        if (subreddit.getName().equals(favouriteSubredditName)) {
                            subreddit.setFavourite(true);
                            break;
                        }
                    }
                }
            }
        }
        else {
            subreddits = Helpers.getDefaultSubredditsFromAsset(context);
        }

        // add front page as the first subreddit
        Subreddit frontPage = new Subreddit();
        frontPage.setName(Globals.DEFAULT_SUBREDDIT);
        subreddits.add(0, frontPage);
        frontPage.setFavourite(true);

        Log.e(TAG, subreddits.size()+"");
        return subreddits;
    }

    private static ArrayList<Subreddit> parseSubreddits(String content, ArrayList<Subreddit> subreddits) {
        try {
            JSONObject main = new JSONObject(content);
            JSONObject data = (JSONObject) main.get("data");
            JSONArray children = (JSONArray) data.get("children");

            Globals.CURRENT_SUBREDDITS_AFTER = !data.isNull("after") ? data.getString("after") : null;

            for (int i=0;i<children.length();i++) {
                JSONObject subreddit_num = (JSONObject) children.get(i);
                JSONObject data2 = (JSONObject) subreddit_num.get("data");

                Subreddit subreddit = new Subreddit();
                subreddit.setName(data2.getString("display_name"));
                subreddit.setNumOfSubscribers(data2.getString("subscribers"));
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

    public static boolean vote(String id, int voteDirection) {
        HttpURLConnection conn = null;

        try {
            URL voteURL = new URL(Globals.API_VOTE_URL);

            conn = Helpers.getConnection(voteURL, "POST", true);

            String postData = "dir=" + voteDirection + "&id=" + id + "&uh=" + Globals.MODHASH;
            Helpers.writeToConnection(conn, postData);

            if (conn.getResponseCode() == 200) {
                return true;
            }
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return false;
    }

}
