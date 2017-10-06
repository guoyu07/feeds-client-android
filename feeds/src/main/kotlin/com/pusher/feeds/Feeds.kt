package com.pusher.feeds

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.pusher.feeds.listeners.FeedsReceivedListener
import com.pusher.feeds.listeners.OnErrorListener
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
            onSuccess: FeedsReceivedListener,
            onFailure: OnErrorListener,
            prefix: String? = null,
            limit: Int? = null){

        var urlBuilder = HttpUrl.Builder().scheme("https").host("pusherplatform.io")
        if(prefix != null) urlBuilder.addQueryParameter("prefix", prefix)
        if(limit != null) urlBuilder.addQueryParameter("limit", limit.toString())

        if(authEndpoint.isNullOrEmpty()){
            throw IllegalStateException("AuthEndpoint must be set io list all the feeds.")
        }

        instance.request(
                options = RequestOptions(
                        method = "GET",
                        path = "/feeds?${ urlBuilder.build().encodedQuery() ?: "" }"
                ),
                tokenProvider = createTokenProvider(),
                tokenParams = FeedsTokenParams(
                        path = "feeds",
                        action = "READ"
                ),
                onSuccess =  { response ->
                    if(response.code() == 200){
                        onSuccess.onFeedsReceived(GSON.fromJson<List<FeedsListItem>>(response.body()!!.charStream(), List::class.java))
                    }
                    else{
                        onFailure.onError(ErrorResponse(
                                statusCode = response.code(),
                                headers = response.headers().toMultimap(),
                                error = "${response.body()}"
                        ))
                    }
                },
                onFailure = { onFailure.onError(it) }
        )
    }

    /**
     * A Java utility to avoid passing everything in a constructor
     * */
    class Builder{

        private var instanceId: String? = null
        private var context: Context? = null
        private var authEndpoint: String? = null
        private var authData: Map<String, String>? = emptyMap()
        private var logLevel: LogLevel? = LogLevel.DEBUG

        fun setInstanceId(instanceId: String): Builder {
            this.instanceId = instanceId
            return this
        }
        fun setContext(context: Context): Builder {
            this.context = context
            return this
        }
        fun setAuthEndpoint(authEndpoint: String): Builder {
            this.authEndpoint = authEndpoint
            return this
        }
        fun setAuthData(authData: Map<String, String>): Builder {
            this.authData = authData
            return this
        }
        fun setLogLevel(logLevel: LogLevel): Builder {
            this.logLevel = logLevel
            return this
        }

        fun build(): Feeds {
            if(instanceId == null){
                throw IllegalStateException("instanceID must be set!")
            }
            if(context == null){
                throw IllegalStateException("context must be set!")
            }

            return Feeds(
                    instanceId = instanceId!!,
                    context = context!!,
                    authEndpoint = authEndpoint,
                    authData = authData!!,
                    logLevel = logLevel!!
            )
        }
    }


}
