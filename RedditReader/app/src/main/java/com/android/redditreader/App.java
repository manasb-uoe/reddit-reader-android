package com.android.redditreader;

import android.app.Application;

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

        //init AsyncImageLoader with default values
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
