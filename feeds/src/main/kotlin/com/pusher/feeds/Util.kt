package com.pusher.feeds

import java.util.*


/**
 * Utility class we can use for mocking
 * Returns current timestamp in seconds from epoch
 * */
class Clock{
    fun currentTimestampInSeconds(): Long {
        return Date().time / 1000
    }
}