package com.android.redditreader.utils;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Manas on 29-12-2014.
 */
public class Helpers {

    private static final  String TAG = Helpers.class.getSimpleName();

    public static HttpURLConnection getConnection(URL url) {
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return urlConnection;
    }

    public static String readContentFromURL(URL url) {
        HttpURLConnection urlConnection = getConnection(url);
        BufferedReader bufferedReader = null;
        String content = null;

        try {
            InputStream inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            content = stringBuilder.toString();
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        return content;
    }

    public static URL getCurrentSubredditURL() {
        String base = Globals.BASE_API_URL;

        if (!Globals.CURRENT_SUBREDDIT.equals(Globals.DEFAULT_SUBREDDIT)) {
            base = base + "/r/" + Globals.CURRENT_SUBREDDIT + "/" + Globals.CURRENT_SORT.toLowerCase() + "/.json";
        }
        else {
            base = base + "/" + Globals.CURRENT_SORT.toLowerCase() + "/.json";
        }

        URL builtURL;
        try {
            Uri.Builder builder = Uri.parse(base).buildUpon();
            builder = Globals.CURRENT_TIME != null ? builder.appendQueryParameter("t", Globals.CURRENT_TIME.toLowerCase()) : builder;
//            builder = builder.appendQueryParameter("limit", String.valueOf(Globals.DEFAULT_LIMIT));
            builder = Globals.CURRENT_POSTS_AFTER != null ? builder.appendQueryParameter("after", Globals.CURRENT_POSTS_AFTER) : builder;
            builtURL = new URL(builder.build().toString());
        }
        catch (MalformedURLException e) {
            builtURL = null;
            Log.e(TAG, e.getMessage());
        }

        return builtURL;
    }

}
