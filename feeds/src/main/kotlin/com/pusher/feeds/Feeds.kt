package com.pusher.feeds

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pusher.platform.Cancelable
import com.pusher.platform.Instance
import com.pusher.platform.RequestOptions
import com.pusher.platform.SubscriptionListeners
import com.pusher.platform.logger.AndroidLogger
import com.pusher.platform.logger.LogLevel
import com.pusher.platform.tokenProvider.TokenProvider
import elements.*
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Response

class Feeds(
        val instanceId: String,
        val authEndpoint: String? = null,
        val authData: Any? = null,
        val logLevel: LogLevel = LogLevel.DEBUG,
        val context: Context
) {

    companion object {
        val GSON = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
    }

    val instance = Instance(
            instanceId = instanceId,
            serviceName = "feeds",
            serviceVersion = "v1",
            logger = AndroidLogger(threshold = logLevel),
            context = context
    )

    //TODO: not private yet.
    fun feed(feedId: String): Feed{
        return Feed(id = feedId, instance = instance)
    }


    //TODO:

    fun list(
            prefix: String? = null,
            limit: Int = -1,
            onSuccess: (List<FeedsListItem>) -> Unit,
            onFailure: (elements.Error) -> Unit){

        val instanceSuccessCallback: (Response) -> Unit = {
            response ->
                if (response.code() == 200){
                    val body = Gson().fromJson<List<FeedsListItem>>(response.body()!!.charStream(), List::class.java)
                    onSuccess(body)
                }
        }



        instance.request(
                options = RequestOptions(
                        method = "GET",
                        path = "feeds"
                ),
                tokenProvider = null, //TODO
                onSuccess =  instanceSuccessCallback,
                onFailure = onFailure
        )

        TODO()
    }

    fun firehose(
            onPublish: (SubscriptionEvent) -> Unit,
            onSubscribe: (SubscriptionEvent) -> Unit,
            onUnsubscribe: (SubscriptionEvent) -> Unit
    ): Cancelable {

        TODO()
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


class Feed(val id: String, val instance: Instance) {

    var subscription: Subscription? = null

    fun subscribe(listeners: FeedSubscriptionListeners, lastEventId: String? = null, previousItems: Int? =
 null) {

        val query = if(previousItems != null) "?previous_items=$previousItems" else ""

        subscription = instance.subscribeResuming(
                path = "feeds/$id/items$query",
                listeners = SubscriptionListeners(
                        onOpen = listeners.onOpen,
                        onError = listeners.onError,
                        onEnd =  listeners.onEnd,
                        onRetrying = listeners.onRetrying,
                        onSubscribe = listeners.onSubscribed,

                        onEvent = { event ->
                            val type = event.body.asJsonObject["type"].asInt
                            if(type == 1){
                                val item = Feeds.GSON.fromJson(event.body.asJsonObject["data"], FeedItem::class.java)
                                listeners.onItem(item)
                            }
                        }
                ),
                initialEventId = lastEventId
        )
    }

    fun unsubscribe(){
        subscription?.unsubscribe()
    }
}

data class FeedItem(val id: String, val created: Long, val data: Any)

data class FeedsListItem(val feedId: String, val length: Int)

data class FeedEvent(val type: Int, val data: EventData)

sealed class EventData

data class PublishEvent(
        val feedId: String,
        val itemId: String,
        val created: String,
        val data: Any
): EventData()

data class SubscribeEvent(val feedId: String, val subscriberId: String): EventData()

class FeedsTokenProvider: TokenProvider {



    override fun fetchToken(tokenParams: Any?, onSuccess: (String) -> Unit, onFailure: (Error) -> Unit): Cancelable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearToken(token: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}