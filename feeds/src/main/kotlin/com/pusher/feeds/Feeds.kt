package com.pusher.feeds

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.pusher.platform.Instance
import com.pusher.platform.RequestOptions
import com.pusher.platform.logger.AndroidLogger
import com.pusher.platform.logger.LogLevel
import com.pusher.platform.tokenProvider.TokenProvider
import elements.*
import okhttp3.HttpUrl
import okhttp3.OkHttpClient

class Feeds(
        val instanceId: String,
        val authEndpoint: String? = null,
        val authData: Map<String, String> = emptyMap(),
        val logLevel: LogLevel = LogLevel.DEBUG,
        val context: Context
) {
    val httpClient = OkHttpClient()

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

    fun feed(feedId: String): Feed {

        val feedTokenProvider: FeedsTokenProvider? =
                if(feedId.startsWith("private-")) createTokenProvider()
                else null

        return Feed(id = feedId, instance = instance, tokenProvider = feedTokenProvider)
    }

    private fun createTokenProvider(): FeedsTokenProvider? =
            if(authEndpoint != null) FeedsTokenProvider(
                endpoint = authEndpoint,
                client = httpClient,
                authData = authData
            ) else null

    data class FeedsListItem(val feedId: String, val length: Int)

    fun list(
            prefix: String? = null,
            limit: Int? = null,
            onSuccess: (List<FeedsListItem>) -> Unit,
            onFailure: (elements.Error) -> Unit){

        var urlBuilder = HttpUrl.Builder().scheme("https").host("pusherplatform.io")
        if(prefix != null) urlBuilder.addQueryParameter("prefix", prefix)
        if(limit != null) urlBuilder.addQueryParameter("limit", limit.toString())

        instance.request(
                options = RequestOptions(
                        method = "GET",
                        path = "/feeds${ urlBuilder.build().encodedQuery() ?: "" }"
                ),
                tokenProvider = createTokenProvider(),
                tokenParams = FeedsTokenParams(
                        path = "feeds",
                        action = "READ"
                ),
                onSuccess =  { response ->
                    if(response.code() == 200){
                        onSuccess(GSON.fromJson<List<FeedsListItem>>(response.body()!!.charStream(), List::class.java))
                    }
                    else{
                        onFailure(ErrorResponse(
                                statusCode = response.code(),
                                headers = response.headers().toMultimap(),
                                error = "${response.body()}"
                        ))
                    }
                },
                onFailure = onFailure
        )
    }
}


data class FeedEvent(val type: Int, val data: EventData)

sealed class EventData

data class PublishEvent(
        val feedId: String,
        val itemId: String,
        val created: String,
        val data: Any
): EventData()

data class SubscribeEvent(val feedId: String, val subscriberId: String): EventData()

