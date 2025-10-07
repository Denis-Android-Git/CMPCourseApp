package com.example.data.network

import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.coroutineContext

actual suspend fun <T> platformSafeCall(
    execute: suspend () -> HttpResponse,
    handleResponse: suspend (HttpResponse) -> CustomResult<T, DataError.Remote>
): CustomResult<T, DataError.Remote> {
    return try {
        val response = execute()
        handleResponse(response)
    } catch (e: UnknownHostException) {
        CustomResult.Failure(DataError.Remote.NO_INTERNET_CONNECTION)
    } catch (e: UnresolvedAddressException) {
        CustomResult.Failure(DataError.Remote.NO_INTERNET_CONNECTION)
    } catch (e: ConnectException) {
        CustomResult.Failure(DataError.Remote.NO_INTERNET_CONNECTION)
    } catch (e: SocketTimeoutException) {
        CustomResult.Failure(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: HttpRequestTimeoutException) {
        CustomResult.Failure(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: SerializationException) {
        CustomResult.Failure(DataError.Remote.SERIALIZATION_ERROR)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        CustomResult.Failure(DataError.Remote.UNKNOWN)
    }
}