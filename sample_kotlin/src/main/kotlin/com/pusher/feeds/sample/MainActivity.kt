package com.pusher.feeds.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.pusher.feeds.Feed
import com.pusher.feeds.FeedSubscriptionListeners
import com.pusher.feeds.Feeds
import com.pusher.feeds.listeners.FeedItemsReceivedListener
import com.pusher.feeds.listeners.FeedsReceivedListener
import com.pusher.feeds.listeners.OnEndListener
import com.pusher.feeds.listeners.OnErrorListener
import com.pusher.feeds.listeners.OnItemListener
import com.pusher.feeds.listeners.OnOpenListener
import com.pusher.feeds.listeners.OnRetryingListener
import com.pusher.feeds.listeners.OnSubscribedListener
import com.pusher.platform.logger.LogLevel
import kotlinx.android.synthetic.main.activity_main.*

import timber.log.Timber

class MainActivity : AppCompatActivity() {

    companion object {

        private val INSTANCE_LOCATOR = ""
        private val AUTH_ENDPONT = "http://10.0.2.2:3000/path/tokens"
        private val PUBLIC_FEED = "my-feed"
        private val PRIVATE_FEED = "private-my-feed"
    }

    lateinit var privateFeed: Feed
    lateinit var publicFeed: Feed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val feeds = Feeds(
                instanceLocator = INSTANCE_LOCATOR,
                context = applicationContext,
                authEndpoint = AUTH_ENDPONT,
                logLevel = LogLevel.VERBOSE
        )

        privateFeed = feeds.feed(PRIVATE_FEED)
        publicFeed = feeds.feed(PUBLIC_FEED)

        subscribePublicButton.setOnClickListener {
            publicFeed.subscribe(
                    FeedSubscriptionListeners(
                            onOpen = OnOpenListener{ Timber.d("onOpen") },
                            onItem = OnItemListener {  feedItem -> Timber.d("onItem ${feedItem.data}") },
                            onError = OnErrorListener { error -> Timber.d("onError $error") },
                            onEnd = OnEndListener { endEvent -> Timber.d("onEnd") },
                            onRetrying = OnRetryingListener { Timber.d("onRetrying") },
                            onSubscribed = OnSubscribedListener { Timber.d("onSubscribed") }
                    )
            )
        }

        unsubscribePublicButton.setOnClickListener { publicFeed.unsubscribe() }

        subscribePrivateButton.setOnClickListener {
            privateFeed.subscribe(
                    FeedSubscriptionListeners(
                            onOpen = OnOpenListener{ Timber.d("onOpen") },
                            onItem = OnItemListener {  feedItem -> Timber.d("onItem ${feedItem.data}") },
                            onError = OnErrorListener { error -> Timber.d("onError $error") }
                    )
            )
        }

        unsubscribePrivateButton.setOnClickListener { privateFeed.unsubscribe() }

        listFeedsButton.setOnClickListener {
            feeds.list(
                    onSuccess = FeedsReceivedListener { feeds -> Timber.d("Feeds received $feeds") },
                    onFailure = OnErrorListener { error -> Timber.d("Error $error") },
                    prefix = "private" //Only list private feeds
            )
        }


        paginatePublicButton.setOnClickListener {
            publicFeed.paginate(
                    onSuccess = FeedItemsReceivedListener { items -> Timber.d("onItems $items") },
                    onError = OnErrorListener { error -> Timber.d("Error $error") }
            )
        }

        paginatePrivateButton.setOnClickListener {
            privateFeed.paginate(
                    onSuccess = FeedItemsReceivedListener { items -> Timber.d("onItems $items") },
                    onError = OnErrorListener { error -> Timber.d("Error $error") }
            )
        }

    }

}
