package com.android.redditreader.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.redditreader.R;
import com.android.redditreader.models.Subreddit;
import com.manas.asyncimageloader.AsyncImageLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manas on 29-12-2014.
 */
public class Helpers {

    private static final  String TAG = Helpers.class.getSimpleName();

    public static HttpURLConnection getConnection(URL url, String requestMethod) {
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(requestMethod);
            if (requestMethod.equals("POST")) {
                urlConnection.setDoOutput(true);
            }
            if (Globals.SESSION_COOKIE != null) {
                urlConnection.setRequestProperty("Cookie", Globals.SESSION_COOKIE);
                Log.e(TAG, "request property set");
            }
            urlConnection.connect();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return urlConnection;
    }

    public static void writeToConnection(HttpURLConnection conn, String postData) {
        OutputStreamWriter osWriter = null;
        try {
            osWriter = new OutputStreamWriter(conn.getOutputStream());
            osWriter.write(postData);
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        finally {
            if (osWriter != null) {
                try {
                    osWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readStringFromConnection(HttpURLConnection urlConnection) {
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

    public static URL getUserSubredditsURL() {
        String base = Globals.BASE_API_URL + "/reddits/mine.json";

        URL builtURL;
        try {
            Uri.Builder builder = Uri.parse(base).buildUpon();
            builder = builder.appendQueryParameter("limit", String.valueOf(Globals.DEFAULT_SUBREDDITS_LIMIT));
            builder = Globals.CURRENT_SUBREDDITS_AFTER != null ? builder.appendQueryParameter("after", Globals.CURRENT_SUBREDDITS_AFTER) : builder;
            builtURL = new URL(builder.build().toString());
        }
        catch (MalformedURLException e) {
            builtURL = null;
            Log.e(TAG, e.getMessage());
        }

        return builtURL;
    }

    public static void viewURLInBrowser(Context context, String urlToView) {
        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.setData(Uri.parse(urlToView));

        // check if intent can be handled
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> infos = packageManager.queryIntentActivities(viewIntent, 0);
        if (infos.size() > 0) {
            context.startActivity(viewIntent);
        }
        else {
            Toast.makeText(context, R.string.error_intent_cannot_be_handled, Toast.LENGTH_LONG).show();
        }
    }

    public static void socialShareLink(Context context, String linkToShare) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, linkToShare);

        // check if intent can be handled
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> infos = packageManager.queryIntentActivities(shareIntent, 0);
        if (infos.size() > 0) {
            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.social_share_dialog_title)));
        }
        else {
            Toast.makeText(context, R.string.error_intent_cannot_be_handled, Toast.LENGTH_LONG).show();
        }
    }

    public static void hideKeyboard(Context context, IBinder token) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(token, 0);
    }

    public static ArrayList<Subreddit> getDefaultSubredditsFromAsset(Context context) {
        final String defaultSubredditsAsset = "default_subreddits.txt";

        BufferedReader bufferedReader = null;
        ArrayList<Subreddit> subreddits = new ArrayList<>();

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(defaultSubredditsAsset)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Subreddit subreddit = new Subreddit();
                subreddit.setName(line);
                subreddits.add(subreddit);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        return subreddits;
    }

    public static void displayThumbnail(String thumbnailURL, ImageView thumbnailImageView) {
        if (thumbnailURL.length() > 0) {
            thumbnailImageView.setVisibility(View.VISIBLE);

            switch (thumbnailURL) {
                case "self":
                    thumbnailImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    AsyncImageLoader.getInstance().displayImage(thumbnailImageView, Globals.THUMBNAIL_SELF);
                    break;

                case "nsfw":
                    thumbnailImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    AsyncImageLoader.getInstance().displayImage(thumbnailImageView, Globals.THUMBNAIL_NSFW);
                    break;

                default:
                    thumbnailImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    AsyncImageLoader.getInstance().displayImage(thumbnailImageView, thumbnailURL);
                    break;
            }
        }
        else {
            thumbnailImageView.setVisibility(View.GONE);
        }
    }

    public static String readFromPreferences(Context context, String fileName, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public static void writeToPreferences(Context context, String fileName, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getUserPreferencesFileName(String username) {
        return username + "_prefs";
    }

}
