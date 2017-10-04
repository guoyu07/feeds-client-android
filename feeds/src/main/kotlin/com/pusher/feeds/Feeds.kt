package com.pusher.feeds

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.pusher.platform.Instance
import com.pusher.platform.RequestOptions
import com.pusher.platform.logger.AndroidLogger
import com.pusher.platform.logger.LogLevel
import elements.*
import okhttp3.HttpUrl
import okhttp3.OkHttpClient

/**
 * Main entry point for the Pusher Feeds client SDK.
 * @param instanceId - the main configuration value for Feeds. Get it from the dashboard dash.pusher.com.
 * @param authEndpoint - path to your deployed authentication endpoint.
 * @param authData extra parameters that will be sent to your authentication service.
 * @param context your Android Application context. Used to detect and react to changes in connectivity when subscribing to a feed.
 * @param logLevel the default logging level.
 * */
class Feeds
    @JvmOverloads constructor(
        val instanceId: String,
        val context: Context,

        val authEndpoint: String? = null,
        val authData: Map<String, String> = emptyMap(),
        val logLevel: LogLevel = LogLevel.DEBUG) {
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

    /**
     * Create a new Feed with a given ID.
     * @param feedId the ID of your feed. Private feeds start with `private-` prefix
     * */
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

    /**
     * List all feeds associated to this instance.
     * @param prefix filters the response and only retrieves feeds with a given prefix
     * @param limit maximum number of feeds to return
     * @param onSuccess callback
     * @param onFailure callback
     * */
    @JvmOverloads fun list(
            onSuccess: (List<FeedsListItem>) -> Unit,
            onFailure: (elements.Error) -> Unit,
            prefix: String? = null,
            limit: Int? = null){

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
