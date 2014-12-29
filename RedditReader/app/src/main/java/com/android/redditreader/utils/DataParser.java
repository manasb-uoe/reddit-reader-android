package com.android.redditreader.utils;

import android.util.Log;

import com.android.redditreader.models.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Manas on 29-12-2014.
 */
public class DataParser {

    private static final String TAG = DataParser.class.getSimpleName();

    public static ArrayList<Post> parsePosts(URL url) {
        ArrayList<Post> posts = null;

        String content = Helpers.readContentFromURL(url);

        if (content != null) {
            try {
                posts = new ArrayList<Post>();

                JSONObject main = new JSONObject(content);
                JSONObject data = (JSONObject) main.get("data");
                JSONArray children = (JSONArray) data.get("children");

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
                    post.setNsfw(Boolean.valueOf(data2.get("over_18").toString()));
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
}
