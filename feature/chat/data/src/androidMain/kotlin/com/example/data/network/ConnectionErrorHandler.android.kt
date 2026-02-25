package com.example.data.network

import com.example.domain.models.ConnectionState
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.websocket.WebSocketException
import io.ktor.network.sockets.SocketTimeoutException
import kotlinx.io.EOFException
import java.net.SocketException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

actual class ConnectionErrorHandler {
    actual fun getConnectionStateFromError(error: Throwable): ConnectionState {
        return when (error) {
            is ClientRequestException,
            is WebSocketException,
            is SocketException,
            is SocketTimeoutException,
            is UnknownHostException,
            is SSLException,
            is EOFException -> ConnectionState.ERROR_NETWORK

            else -> ConnectionState.ERROR_UNKNOWN
        }
    }

    actual fun transformException(error: Throwable): Throwable {
        return error
    }

    actual fun isRetryableError(error: Throwable): Boolean {
        return when (error) {
            is WebSocketException,
            is SocketException,
            is SocketTimeoutException,
            is EOFException -> true

            else -> false
        }
    }
}