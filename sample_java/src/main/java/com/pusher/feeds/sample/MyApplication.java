package com.pusher.feeds.sample;

import android.app.Application;

import com.pusher.feeds.Feeds;

import timber.log.Timber;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}

