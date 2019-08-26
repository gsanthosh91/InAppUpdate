package com.gsanthosh91.inappupdate;

import android.app.Application;
import android.content.Context;

import com.downloader.PRDownloader;

public class MvpApplication extends Application {

    private static MvpApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        PRDownloader.initialize(getApplicationContext());
    }

    public static synchronized MvpApplication getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

}
