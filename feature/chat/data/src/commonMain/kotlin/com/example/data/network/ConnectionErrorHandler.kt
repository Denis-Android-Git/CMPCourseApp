package com.example.data.network

import com.example.domain.models.ConnectionState

expect class ConnectionErrorHandler {
    fun getConnectionStateFromError(error: Throwable): ConnectionState
    fun transformException(error: Throwable): Throwable
    fun isRetryableError(error: Throwable): Boolean
}