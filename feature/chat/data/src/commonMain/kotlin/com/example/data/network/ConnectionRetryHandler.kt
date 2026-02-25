package com.example.data.network

import kotlinx.coroutines.delay
import kotlin.math.pow

class ConnectionRetryHandler(
    private val connectionErrorHandler: ConnectionErrorHandler
) {
    private var shouldSkipDelay = false

    fun shouldRetry(error: Throwable, attempt: Long): Boolean {
        return connectionErrorHandler.isRetryableError(error)
    }

    suspend fun applyRetryDelay(attempt: Long) {
        if (!shouldSkipDelay) {
            val delay = createBackOffDelay(attempt)
            delay(delay)
        } else {
            shouldSkipDelay = false
        }
    }

    fun resetDelay() {
        shouldSkipDelay = true
    }

    private fun createBackOffDelay(attempt: Long): Long {
        val delayTime = (2f.pow(attempt.toInt()) * 2000L).toLong()
        val maxDelay = 30000L
        return minOf(delayTime, maxDelay)
    }
}