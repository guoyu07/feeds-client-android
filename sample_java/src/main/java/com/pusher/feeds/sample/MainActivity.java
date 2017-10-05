package com.pusher.feeds.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pusher.feeds.Feed;
import com.pusher.feeds.FeedItem;
import com.pusher.feeds.FeedSubscriptionListeners;
import com.pusher.feeds.Feeds;
import com.pusher.feeds.listeners.OnErrorListener;
import com.pusher.feeds.listeners.OnItemListener;
import com.pusher.feeds.listeners.OnOpenListener;
import com.pusher.platform.logger.LogLevel;

import java.util.List;
import java.util.Map;

import elements.Error;
import elements.Subscription;

public class MainActivity extends AppCompatActivity {

    private static final String INSTANCE_ID = "v1:us1:8ab984e1-ea05-4c9e-8876-f3be088e2d01";
    private static final String AUTH_ENDPONT = "http://10.0.2.2:3000/path/tokens";
    private static final String PUBLIC_FEED = "my-feed";
    private static final String PRIVATE_FEED = "private-my-feed";

    private Feed privateFeed;
    private Feed publicFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Feeds feeds = new Feeds(INSTANCE_ID, getApplicationContext(), AUTH_ENDPONT, null, LogLevel.VERBOSE);

        privateFeed = feeds.feed(PRIVATE_FEED);
        publicFeed = feeds.feed(PUBLIC_FEED);

        findViewById(R.id.subscribe_public_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicFeed.subscribe(new FeedSubscriptionListeners(
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
                        new OnErrorListener() {
                            @Override
                            public void onError(@NonNull Error error) {

                            }
                        }
                ));
            }
        });

        findViewById(R.id.subscribe_private_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                privateFeed.subscribe(new FeedSubscriptionListeners(
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
                        new OnErrorListener() {
                            @Override
                            public void onError(@NonNull Error error) {

                            }
                        }
                ));
            }
        });

//        findViewById(R.id.paginate_private_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                privateFeed.paginate();
//            }
//        });
    }




}
