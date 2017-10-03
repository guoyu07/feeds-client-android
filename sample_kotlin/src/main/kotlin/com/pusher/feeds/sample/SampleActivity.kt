package com.pusher.feeds.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.pusher.feeds.FeedSubscriptionListeners
import com.pusher.feeds.Feeds
import com.pusher.feeds.R
import com.pusher.platform.logger.LogLevel

class SampleActivity : AppCompatActivity() {

    val INSTANCE_ID = "v1:us1:8ab984e1-ea05-4c9e-8876-f3be088e2d01"
    val TAG = "FEEDS_SAMPLE"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        val feeds = Feeds(
                instanceId = INSTANCE_ID,
                context = this,
                logLevel = LogLevel.VERBOSE,
                authEndpoint = "http://10.0.2.2:3000/path/tokens"

        )

        val feed = feeds.feed("private-my-feed")

        feed.subscribe(
                FeedSubscriptionListeners(
                        onOpen = { headers -> Log.d(TAG, "onOpen: $headers") },
                        onItem = { item -> Log.d(TAG, "$item") },
                        onError = { error -> Log.d(TAG, "$error") }
                )
        )


//        feeds.list(
//                onSuccess = { feeds -> Log.d(TAG, "FEEDS! $feeds") },
//                onFailure = { error -> Log.d(TAG, "$error")}
//        )



    }
}
