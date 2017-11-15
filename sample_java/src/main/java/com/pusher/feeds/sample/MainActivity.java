package com.pusher.feeds.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pusher.feeds.Feed;
import com.pusher.feeds.FeedItem;
import com.pusher.feeds.FeedPaginationResponse;
import com.pusher.feeds.FeedSubscriptionListeners;
import com.pusher.feeds.Feeds;
import com.pusher.feeds.listeners.FeedItemsReceivedListener;
import com.pusher.feeds.listeners.FeedsReceivedListener;
import com.pusher.feeds.listeners.OnErrorListener;
import com.pusher.feeds.listeners.OnItemListener;
import com.pusher.feeds.listeners.OnOpenListener;
import com.pusher.platform.logger.LogLevel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import elements.Error;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String INSTANCE_LOCATOR = "";
    private static final String AUTH_ENDPONT = "http://10.0.2.2:3000/path/tokens";
    private static final String PUBLIC_FEED = "my-feed";
    private static final String PRIVATE_FEED = "private-my-feed";

    private Feed privateFeed;
    private Feed publicFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Feeds feeds = new Feeds.Builder()
                .setInstanceLocator(INSTANCE_LOCATOR)
                .setContext(getApplicationContext())
                .setAuthEndpoint(AUTH_ENDPONT)
                .setLogLevel(LogLevel.VERBOSE)
                .build();

        privateFeed = feeds.feed(PRIVATE_FEED);
        publicFeed = feeds.feed(PUBLIC_FEED);

        findViewById(R.id.subscribe_public_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicFeed.subscribe(new FeedSubscriptionListeners(
                        new OnOpenListener() {
                            @Override
                            public void onOpen(@NonNull Map<String, List<String>> headers) {
                                Timber.d("onOpen");
                            }
                        },
                        new OnItemListener() {
                            @Override
                            public void onItem(@NonNull FeedItem item) {
                                Timber.d("onItem %s", item.getData().toString());

                            }
                        },
                        new OnErrorListener() {
                            @Override
                            public void onError(@NonNull Error error) {
                                Timber.d("onError %s", error.toString());
                            }
                        }
                ));
            }
        });

        findViewById(R.id.unsubscribe_public_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicFeed.unsubscribe();
            }
        });

        findViewById(R.id.subscribe_private_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                privateFeed.subscribe(new FeedSubscriptionListeners(
                        new OnOpenListener() {
                            @Override
                            public void onOpen(@NonNull Map<String, List<String>> headers) {
                                Timber.d("onOpen");

                            }
                        },
                        new OnItemListener() {
                            @Override
                            public void onItem(@NonNull FeedItem item) {
                                Timber.d("onItem %s", item.getData().toString());

                            }
                        },
                        new OnErrorListener() {
                            @Override
                            public void onError(@NonNull Error error) {
                                Timber.d("onError %s", error.toString());

                            }
                        }
                ));
            }
        });

        findViewById(R.id.unsubscribe_private_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privateFeed.unsubscribe();
            }
        });

        findViewById(R.id.list_feeds_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feeds.list(
                        new FeedsReceivedListener() {
                            @Override
                            public void onFeedsReceived(List<Feeds.FeedsListItem> feeds) {
                                Timber.d("onFeedsReceived %s", feeds.toString());
                            }
                        },
                        new OnErrorListener() {
                            @Override
                            public void onError(@NonNull Error error) {
                                Timber.d("onError %s", error.toString());
                            }
                        }, "private"
                );
            }
        });



        findViewById(R.id.paginate_private_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privateFeed.paginate(
                        new FeedItemsReceivedListener() {
                            @Override
                            public void onItems(FeedPaginationResponse items) {
                                Timber.d("onItems %s", items.toString());
                            }
                        }, new OnErrorListener() {
                            @Override
                            public void onError(@NonNull Error error) {
                                Timber.d("onError %s", error.toString());
                            }
                        }
                );
            }
        });

        findViewById(R.id.paginate_public_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicFeed.paginate(
                        new FeedItemsReceivedListener() {
                            @Override
                            public void onItems(FeedPaginationResponse items) {
                                Timber.d("onItems %s", items.toString());
                            }
                        }, new OnErrorListener() {
                            @Override
                            public void onError(@NonNull Error error) {
                                Timber.d("onError %s", error.toString());
                            }
                        }
                );
            }
        });
    }




}
