package com.pusher.feeds

/**
 * Default token expiry tolerance - 10 minutes
 * */
val CACHE_EXPIRY_TOLERANCE = 10*60

interface TokenCache {
    /**
     * Store the current valid token in a local cache.
     * @param token the token value to store
     * @param expiresIn seconds until token expiry.
     * */
    fun cache(token: String, expiresIn: Long)


    /**
     * Get the currently cached token, if any
     * @return the token that is currently cached, if not expired, or null if there is no token, or the token is expired
     */
    fun getTokenFromCache(): String?

    /**
     * Clear the currently stored token from cache
     * */
    fun clearCache()
}

/**
 * A simple in-memory cache implementation
 * */
class InMemoryTokenCache(val clock: Clock): TokenCache{
    var token: String? = null
    var expiration: Long = -1


    override fun cache(token: String, expiresIn: Long){

        this.token = token
        this.expiration = clock.currentTimestampInSeconds() + expiresIn - CACHE_EXPIRY_TOLERANCE
    }

    override fun getTokenFromCache(): String? {

        val now = clock.currentTimestampInSeconds()

        return if(token != null && now < expiration) token
        else null

    }

    override fun clearCache() {
        token = null
        expiration = -1
    }
}