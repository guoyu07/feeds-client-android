package com.pusher.feeds

import com.pusher.feeds.Feeds.Companion.GSON
import com.pusher.platform.Cancelable
import com.pusher.platform.tokenProvider.TokenProvider
import elements.Error
import elements.NetworkError
import okhttp3.*
import java.io.IOException


class FeedsTokenProvider(
        val endpoint: String,
        val authData: Map<String, String> = emptyMap(),
        val client: OkHttpClient,
        val tokenCache: TokenCache = InMemoryTokenCache(Clock())): TokenProvider {

    var call: Call? = null

    override fun fetchToken(tokenParams: Any?, onSuccess: (String) -> Unit, onFailure: (Error) -> Unit): Cancelable {

        val cachedToken = tokenCache.getTokenFromCache()

        return if(cachedToken != null){
            onSuccess(cachedToken)
            object: Cancelable {
                override fun cancel() {} //Nothing to cancel, we can ignore.
            }
        }
        else fetchTokenFromEndpoint(
                tokenParams = tokenParams,
                onFailure = onFailure,
                onSuccess = { token ->
                    tokenCache.cache(token.accessToken, token.expiresIn.toLong())
                    onSuccess(token.accessToken)
                })
    }

    override fun clearToken(token: String?) {
        tokenCache.clearCache()
    }


    private fun fetchTokenFromEndpoint(tokenParams: Any?, onSuccess: (FeedsTokenResponse) -> Unit, onFailure: (Error) -> Unit): Cancelable {

        if (tokenParams is FeedsTokenParams) {

            val requestBodyBuilder = FormBody.Builder()
                    .add("path", tokenParams.path)
                    .add("action", tokenParams.action)
                    .add("grant_type", "client_credentials")

            //Add any extras to the token provider's request.
            authData.keys.forEach { key ->
                requestBodyBuilder.add(key, authData.getValue(key))
            }

            tokenParams.extras.keys.forEach { key ->
                requestBodyBuilder.add(key, tokenParams.extras.getValue(key))
            }
            
            val requestBody = requestBodyBuilder.build()

            val request = Request.Builder()
                    .url(endpoint)
                    .post(requestBody)
                    .build()

            call = client.newCall(request)

            call!!.enqueue( object: Callback {

                override fun onResponse(call: Call?, response: Response?) {

                    if(response != null && response.code() == 200) {
                        val token = GSON.fromJson<FeedsTokenResponse>(response.body()!!.charStream(), FeedsTokenResponse::class.java)

                        tokenCache.cache(token.accessToken, token.expiresIn.toLong())
                        onSuccess(token)
                    }

                    else{
                        onFailure(elements.ErrorResponse(
                                statusCode = response!!.code(),
                                headers = response.headers().toMultimap(),
                                error = response.body().toString()
                        ))
                    }
                }

                override fun onFailure(call: Call?, e: IOException?) {
                    onFailure(NetworkError("Failed! $e"))
                }

            })
        }
        else{
            throw kotlin.Error("Wrong token params!")
        }

        return object: Cancelable {
            override fun cancel() {
                call?.cancel()
            }
        }
    }
}

data class FeedsTokenResponse(

        val accessToken: String,
        val tokenType: String,
        val expiresIn: String,
        val refreshToken: String
)

data class FeedsTokenParams(
        val path: String,
        val action: String = "READ",
        val extras: Map<String, String> = emptyMap()
)
