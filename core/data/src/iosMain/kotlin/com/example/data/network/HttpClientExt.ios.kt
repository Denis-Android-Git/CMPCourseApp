package com.example.data.network

import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import io.ktor.client.engine.darwin.DarwinHttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import platform.Foundation.NSURLErrorCallIsActive
import platform.Foundation.NSURLErrorCannotFindHost
import platform.Foundation.NSURLErrorDNSLookupFailed
import platform.Foundation.NSURLErrorDataNotAllowed
import platform.Foundation.NSURLErrorDomain
import platform.Foundation.NSURLErrorInternationalRoamingOff
import platform.Foundation.NSURLErrorNetworkConnectionLost
import platform.Foundation.NSURLErrorNotConnectedToInternet
import platform.Foundation.NSURLErrorResourceUnavailable
import platform.Foundation.NSURLErrorTimedOut
import kotlin.coroutines.coroutineContext

actual suspend fun <T> platformSafeCall(
    execute: suspend () -> HttpResponse,
    handleResponse: suspend (HttpResponse) -> CustomResult<T, DataError.Remote>
): CustomResult<T, DataError.Remote> {
    return try {
        val response = execute()
        handleResponse(response)
    } catch (e: DarwinHttpRequestException) {
        handleDarwinException(e)
    } catch (e: UnresolvedAddressException) {
        CustomResult.Failure(DataError.Remote.NO_INTERNET_CONNECTION)
    } catch (e: HttpRequestTimeoutException) {
        CustomResult.Failure(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: SerializationException) {
        CustomResult.Failure(DataError.Remote.SERIALIZATION_ERROR)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        CustomResult.Failure(DataError.Remote.UNKNOWN)
    }
}

private fun handleDarwinException(e: DarwinHttpRequestException): CustomResult<Nothing, DataError.Remote> {
    val nsError = e.origin
    return if (nsError.domain == NSURLErrorDomain) {
        when (nsError.code) {
            NSURLErrorNotConnectedToInternet,
            NSURLErrorNetworkConnectionLost,
            NSURLErrorCannotFindHost,
            NSURLErrorDNSLookupFailed,
            NSURLErrorResourceUnavailable,
            NSURLErrorInternationalRoamingOff,
            NSURLErrorCallIsActive,
            NSURLErrorDataNotAllowed -> {
                return CustomResult.Failure(DataError.Remote.NO_INTERNET_CONNECTION)
            }

            NSURLErrorTimedOut -> {
                return CustomResult.Failure(DataError.Remote.REQUEST_TIMEOUT)
            }

            else -> {
                return CustomResult.Failure(DataError.Remote.UNKNOWN)
            }
        }
    } else CustomResult.Failure(DataError.Remote.UNKNOWN)
}