package com.pusher.feeds

import com.pusher.platform.Instance
import com.pusher.platform.SubscriptionListeners
import com.pusher.platform.tokenProvider.TokenProvider
import elements.EOSEvent
import elements.Error
import elements.Headers
import elements.Subscription

class Feed(val id: String, val instance: Instance, val tokenProvider: FeedsTokenProvider? = null) {

    var subscription: Subscription? = null


    fun subscribe(listeners: FeedSubscriptionListeners, lastEventId: String? = null, previousItems: Int? =
 null) {

        val tokenParams: FeedsTokenParams? =
                if(tokenProvider != null) FeedsTokenParams("feeds/$id/items")
                else null

        val query = if(previousItems != null) "?previous_items=$previousItems" else ""

        subscription = instance.subscribeResuming(
                path = "feeds/$id/items$query",
                listeners = SubscriptionListeners(
                        onOpen = listeners.onOpen,
                        onError = listeners.onError,
                        onEnd = listeners.onEnd,
                        onRetrying = listeners.onRetrying,
                        onSubscribe = listeners.onSubscribed,
                        onEvent = { event ->
                            val type = event.body.asJsonObject["type"].asInt
                            if (type == 1) {
                                val item = Feeds.GSON.fromJson(event.body.asJsonObject["data"], FeedItem::class.java)
                                listeners.onItem(item)
                            }
                        }
                ),
                tokenProvider = tokenProvider,
                tokenParams = tokenParams,
                initialEventId = lastEventId
        )
    }

    fun unsubscribe(){
        subscription?.unsubscribe()
    }
}

data class FeedSubscriptionListeners(
        val onOpen: (Headers) -> Unit,
        val onItem: (FeedItem) -> Unit,
        val onError: (Error) -> Unit = {},
        val onEnd: (EOSEvent?) -> Unit = {},
        val onRetrying: () -> Unit = {},
        val onSubscribed: () -> Unit = {}
)

data class FeedItem(val id: String, val created: Long, val data: Any)