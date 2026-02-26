package com.example.data.network

import com.example.domain.models.ConnectionState
import platform.Foundation.NSError
import platform.Foundation.NSURLErrorDomain
import platform.Foundation.NSURLErrorNetworkConnectionLost
import platform.Foundation.NSURLErrorNotConnectedToInternet
import platform.Foundation.NSURLErrorTimedOut
import kotlin.coroutines.cancellation.CancellationException

actual class ConnectionErrorHandler {
    actual fun getConnectionStateFromError(error: Throwable): ConnectionState {
        val nsError = extractNsError(error)

        return if (nsError != null) {
            when (nsError.code) {
                NSURLErrorNotConnectedToInternet,
                NSURLErrorNetworkConnectionLost,
                NSURLErrorTimedOut -> ConnectionState.ERROR_NETWORK

                else -> ConnectionState.ERROR_UNKNOWN
            }
        } else if (error is IOSNetworkCancellationException) {
            ConnectionState.ERROR_NETWORK
        } else ConnectionState.ERROR_UNKNOWN
    }

    actual fun transformException(error: Throwable): Throwable {
        if (error is CancellationException) {
            val cause = error.cause ?: return error
            val isDarwinException = cause.message?.contains("DarwinHttpRequestException") == true
            val isConnectionLostException =
                cause.message?.contains("NSURLErrorDomain Code=-1005") == true
            val isNotConnectedException =
                cause.message?.contains("NSURLErrorDomain Code=-1009") == true

            if (isDarwinException || isConnectionLostException || isNotConnectedException) {
                return IOSNetworkCancellationException(
                    message = "Network connection lost (extracted from cancellation)",
                    cause = cause
                )
            }
        }

        return error
    }

    actual fun isRetryableError(error: Throwable): Boolean {
        if (error is IOSNetworkCancellationException) {
            return true
        }

        return when (extractNsError(error)?.code) {
            NSURLErrorNotConnectedToInternet,
            NSURLErrorNetworkConnectionLost,
            NSURLErrorTimedOut -> true

            else -> false
        }
    }

    private fun extractNsError(cause: Throwable): NSError? {
        val exceptionNsError = cause.toNSError()
        val causeNsError = cause.cause?.toNSError()

        return exceptionNsError ?: causeNsError
    }

    private fun Throwable.toNSError(): NSError? {
        return message?.let { message ->
            when {
                message.contains(NSURLErrorNotConnectedToInternetPattern) ->
                    return NSError.errorWithDomain(
                        domain = NSURLErrorDomain,
                        code = NSURLErrorNotConnectedToInternet,
                        userInfo = null
                    )

                message.contains(NSURLErrorNetworkConnectionLostPattern) ->
                    return NSError.errorWithDomain(
                        domain = NSURLErrorDomain,
                        code = NSURLErrorNetworkConnectionLost,
                        userInfo = null
                    )

                else -> null
            }
        }
    }

    companion object {
        private val NSURLErrorNotConnectedToInternetPattern =
            "Error Domain=${NSURLErrorDomain} Code=${NSURLErrorNotConnectedToInternet}"
        val NSURLErrorNetworkConnectionLostPattern =
            "Error Domain=${NSURLErrorDomain} Code=${NSURLErrorNetworkConnectionLost}"
    }

}

class IOSNetworkCancellationException(
    message: String,
    cause: Throwable?
) : Exception(message, cause)