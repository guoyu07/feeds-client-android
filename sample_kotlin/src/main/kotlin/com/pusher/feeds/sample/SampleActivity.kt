package com.pusher.feeds.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.pusher.feeds.*
import com.pusher.feeds.listeners.OnErrorListener
import com.pusher.feeds.listeners.OnItemListener
import com.pusher.feeds.listeners.OnOpenListener
import com.pusher.platform.logger.LogLevel

class SampleActivity : AppCompatActivity() {

    val INSTANCE_ID = "v1:us1:8ab984e1-ea05-4c9e-8876-f3be088e2d01"
    val AUTH_ENDPOINT = "http://10.0.2.2:3000/path/tokens"
    val TAG = "FEEDS_SAMPLE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        val feeds = Feeds(
                instanceId = INSTANCE_ID,
                context = this,
                logLevel = LogLevel.VERBOSE,
                authEndpoint = AUTH_ENDPOINT
        )

        val feed = feeds.feed("private-my-feed")

        feed.subscribe(
                FeedSubscriptionListeners(
                        onOpen = OnOpenListener { headers -> Log.d(TAG, "onOpen: $headers") },
                        onItem = OnItemListener { item -> Log.d(TAG, "$item") },
                        onError = OnErrorListener { error -> Log.d(TAG, "$error") }
                )
        )


        feeds.list(
                onSuccess = { feeds -> Log.d(TAG, "FEEDS! $feeds") },
                onFailure = { error -> Log.d(TAG, "$error")}
        )

        feed.paginate(
                onSuccess = { feeds -> Log.d(TAG, "All the feed items! $feeds") },
                onError = { error -> Log.d(TAG, "$error")}
        )



    }
}
