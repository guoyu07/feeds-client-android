package com.pusher.feeds.sample;

import android.arch.core.util.Function;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pusher.feeds.Feed;
import com.pusher.feeds.FeedItem;
import com.pusher.feeds.FeedSubscriptionListeners;
import com.pusher.feeds.Feeds;
import com.pusher.feeds.listeners.OnEndListener;
import com.pusher.feeds.listeners.OnErrorListener;
import com.pusher.feeds.listeners.OnItemListener;
import com.pusher.feeds.listeners.OnOpenListener;
import com.pusher.feeds.listeners.OnRetryingListener;
import com.pusher.feeds.listeners.OnSubscribedListener;
import com.pusher.platform.logger.LogLevel;

import java.util.List;
import java.util.Map;

import elements.EOSEvent;
import elements.Error;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Feeds feeds = new Feeds(
                "v1:us1:8ab984e1-ea05-4c9e-8876-f3be088e2d01",
                "http://10.0.2.2:3000/path/tokens",
                null,
                LogLevel.VERBOSE,
                getApplicationContext()
        );

        Feed feed = feeds.feed("private-my-feed");

        feed.subscribe(new FeedSubscriptionListeners(
                new OnOpenListener() {
                    @Override
                    public void onOpen(@NonNull Map<String, List<String>> headers) {

                    }
                },
                new OnItemListener() {
                    @Override
                    public void onItem(@NonNull FeedItem item) {

                    }
                },
                new OnErrorListener(
                ) {
                    @Override
                    public void onError(@NonNull Error error) {

                    }
                },
                new OnEndListener() {
                    @Override
                    public void onEnd(@Nullable EOSEvent endEvent) {

                    }
                },
                new OnRetryingListener() {
                    @Override
                    public void onRetrying() {

                    }
                },
                new OnSubscribedListener() {
                    @Override
                    public void onSubscribed() {

                    }
                }
        ), null, null);

//        FeedSubscriptionListeners listeners = new FeedSubscriptionListeners(
//        )


    }
}
