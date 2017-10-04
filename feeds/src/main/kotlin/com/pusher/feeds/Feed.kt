package com.pusher.feeds

import com.pusher.feeds.listeners.*
import com.pusher.platform.Instance
import com.pusher.platform.RequestOptions
import com.pusher.platform.SubscriptionListeners
import elements.*
import okhttp3.HttpUrl
import java.util.*

/**
 * A single feed. Allows subscribing, unsubscribing, and paginating items already in the feed. Create using the Feeds.feed
 * @param id feed ID. If it starts with `private-` then the feed will be private and require a tokenProvider to be set, otherwise the feed will be public.
 * @param instance current Pusher Platform instance
 * @param tokenProvider the source of tokens
 * */
class Feed
    @JvmOverloads constructor(
        private val id: String,
        private val instance: Instance,
        private val tokenProvider: FeedsTokenProvider? = null) {

    private var subscription: Subscription? = null
    private var openHeaders: Headers = TreeMap()
    var openedForTheFirstTime: Boolean = false

    /**
     * Subscribe to this feed. If a connection is interrupted it will retry subscribing from the last known item.
     * @param listeners the subscription callbacks
     * @param lastEventId last known event ID you wish the items to be retrieved from. Takes priority over previousItems argument
     * @param previousItems number of previous items to retrieve. Will be ignored if lastEventId is set.
     * */
    @JvmOverloads fun subscribe(
            listeners: FeedSubscriptionListeners,
            lastEventId: String? = null,
            previousItems: Int? =
 null) {

        val tokenParams: FeedsTokenParams? =
                if(tokenProvider != null) FeedsTokenParams("feeds/$id/items")
                else null

        val query = if(previousItems != null) "?previous_items=$previousItems" else ""

        subscription = instance.subscribeResuming(
                path = "feeds/$id/items$query",
                listeners = SubscriptionListeners(
                        onOpen = { openHeaders = it },
                        onError = {listeners.onError.onError(it)},
                        onEnd = { listeners.onEnd.onEnd(it)},
                        onRetrying = { listeners.onRetrying.onRetrying() },
                        onSubscribe = { listeners.onSubscribed.onSubscribed() },
                        onEvent = { event ->
                            val type = event.body.asJsonObject["type"].asInt

                            //Open subtype
                            if( type == 0 && !openedForTheFirstTime){
                                openedForTheFirstTime = true

                                val openEvent = Feeds.GSON.fromJson(event.body.asJsonObject["data"], FeedOpenEvent::class.java)
                                openHeaders.put("next_cursor", listOf(openEvent.nextCursor))
                                openHeaders.put("remaining", listOf(openEvent.remaining.toString()))
                                listeners.onOpen.onOpen(openHeaders)
                            }
                            if (type == 1) {
                                val item = Feeds.GSON.fromJson(event.body.asJsonObject["data"], FeedItem::class.java)
                                listeners.onItem.onItem(item)
                            }

                        }
                ),
                tokenProvider = tokenProvider,
                tokenParams = tokenParams,
                initialEventId = lastEventId
        )
    }

    private data class FeedOpenEvent(val nextCursor: String, val remaining: Int)

    /**
     * Cancel the current subscription, if any.
     * */
    fun unsubscribe(){
        subscription?.unsubscribe()
    }

    /**
     * Get historic items in the feed.
     * @param cursor the newest item to retrieve. If null the most recent item in the feed will be fetched.
     * @param limit number of feed items to retrieve.
     * @param onSuccess callback.
     * @param onError callback.
     * */
    @JvmOverloads
    fun paginate(
            onSuccess: (FeedPaginationResponse) -> Unit,
            onError: (elements.Error) -> Unit,
            cursor: String? = null,
            limit: Int = 50){


        var urlBuilder = HttpUrl.Builder().scheme("https").host("pusherplatform.io")
        if(cursor != null) urlBuilder.addQueryParameter("cursor", cursor)
        urlBuilder.addQueryParameter("limit", "$limit")
        val resourcePath = "feeds/$id/items"
        val path = resourcePath.plus("?").plus(urlBuilder.build().encodedQuery())

        instance.request(
                options = RequestOptions(
                        method = "GET",
                        path = path
                ),
                tokenProvider = tokenProvider,
                tokenParams = FeedsTokenParams(
                        path = resourcePath
                ),
                onSuccess = { response ->
                    if(response.code() == 200){
                        onSuccess(Feeds.GSON.fromJson<FeedPaginationResponse>(response.body()!!.charStream(), FeedPaginationResponse::class.java))
                    }
                    else {
                        onError(ErrorResponse(
                                statusCode = response.code(),
                                headers = response.headers().toMultimap(),
                                error = response.body()!!.string()
                        ))
                    }
                },
                onFailure = {
                    onError(NetworkError(it.toString()))
                }
        )

    }
}


/**
 * Feed subscription listeners.
 * @param onOpen called when the feed is first subscribed. Mandatory.
 * @param onItem called with each item received. Mandatory.
 * @param onError called when the feed terminates with an error. Optional.
 * @param onEnd called when the feed terminates successfully. Optional.
 * @param onRetrying called when the feed has disconnected, but is trying to reconnect. Can be used to update connection status. Optional.
 * @param onSubscribed called when the feed has successfully opened a subscription. Can be used to update connection status. Optional
 * */
data class FeedSubscriptionListeners @JvmOverloads constructor(
        val onOpen: OnOpenListener,
        val onItem: OnItemListener,
        val onError: OnErrorListener = OnErrorListener {  },
        val onEnd: OnEndListener = OnEndListener {  },
        val onRetrying: OnRetryingListener = OnRetryingListener {  },
        val onSubscribed: OnSubscribedListener = OnSubscribedListener {  }
)

/**
 * A single Feed item.
 * @param id unique identifier.
 * @param created timestamp of this item's creation in seconds from Epoch.
 * @param data the data held in this feed item.
 * */
data class FeedItem(val id: String, val created: Long, val data: Any)

/**
 * A successful pagination response from Feed.paginate().
 * @param items list of items in the result.
 * @param nextCursor next item in this feed, if any.
 * @param remaining number of items remaining in this feed.
 * */
data class FeedPaginationResponse(
        val items: List<FeedItem>,
        val nextCursor: String?,
        val remaining: Int
)