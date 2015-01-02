package com.android.redditreader;

import android.app.Application;

import com.android.redditreader.utils.Globals;
import com.android.redditreader.utils.Helpers;
import com.manas.asyncimageloader.AsyncImageLoader;
import com.manas.asyncimageloader.AsyncImageLoaderConfig;

import java.io.File;

/**
 * Created by Manas on 30-12-2014.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // update global SESSION_COOKIE with last authenticated user's session cookie
        String lastUsername = Helpers.readFromPreferences(this, Globals.GLOBAL_PREFS, Globals.GLOBAL_PREFS_LAST_USERNAME_KEY);
        Globals.SESSION_COOKIE = Helpers.readFromPreferences(this, Helpers.getUserPreferencesFileName(lastUsername), Globals.USER_PREFS_SESSION_COOKIE_KEY);

        // init AsyncImageLoader
        AsyncImageLoaderConfig config = new AsyncImageLoaderConfig.Builder(this)
                .setMemoryCacheSize((int) (Runtime.getRuntime().maxMemory() / 8))
                .setDiskCacheLocation(new File(getExternalCacheDir().getAbsolutePath() + "/images"))
                .setThreadPoolSize(Runtime.getRuntime().availableProcessors() + 1)
                .setPlaceHolderImage(0)
                .setShouldFadeIn(true)
                .setFadeInDuration(150)
                .build();
        AsyncImageLoader.initalize(config);
    }
}
