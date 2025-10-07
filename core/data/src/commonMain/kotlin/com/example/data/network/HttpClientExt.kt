package com.example.data.network

import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url

suspend inline fun <reified Request, reified Response : Any> HttpClient.post(
    route: String,
    queryParams: Map<String, Any> = mapOf(),
    body: Request,
    crossinline builder: HttpRequestBuilder.() -> Unit = {}
): CustomResult<Response, DataError.Remote> {
    return safeCall {
        post {
            url(constructRoute(route))
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
            setBody(body)
            builder()
        }
    }
}

suspend inline fun <reified Request, reified Response : Any> HttpClient.put(
    route: String,
    queryParams: Map<String, Any> = mapOf(),
    body: Request,
    crossinline builder: HttpRequestBuilder.() -> Unit = {}
): CustomResult<Response, DataError.Remote> {
    return safeCall {
        put {
            url(constructRoute(route))
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
            setBody(body)
            builder()
        }
    }
}
suspend inline fun <reified Response : Any> HttpClient.get(
    route: String,
    queryParams: Map<String, Any> = mapOf(),
    crossinline builder: HttpRequestBuilder.() -> Unit = {}
): CustomResult<Response, DataError.Remote> {
    return safeCall {
        get {
            url(constructRoute(route))
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
            builder()
        }
    }
}


suspend inline fun <reified Response : Any> HttpClient.delete(
    route: String,
    queryParams: Map<String, Any> = mapOf(),
    crossinline builder: HttpRequestBuilder.() -> Unit = {}
): CustomResult<Response, DataError.Remote> {
    return safeCall {
        delete {
            url(constructRoute(route))
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
            builder()
        }
    }
}

fun constructRoute(route: String): String {
    return when {
        route.contains(UrlConstant.BASE_URL) -> route
        route.startsWith("/") -> "{UrlConstant.BASE_URL}$route"
        else -> "{UrlConstant.BASE_URL}/$route"
    }
}

suspend inline fun <reified T> responseToResult(response: HttpResponse): CustomResult<T, DataError.Remote> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                CustomResult.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                CustomResult.Failure(DataError.Remote.SERIALIZATION_ERROR)
            }
        }

        400 -> CustomResult.Failure(DataError.Remote.BAD_REQUEST)
        401 -> CustomResult.Failure(DataError.Remote.UNAUTHORIZED)
        403 -> CustomResult.Failure(DataError.Remote.FORBIDDEN)
        404 -> CustomResult.Failure(DataError.Remote.NOT_FOUND)
        408 -> CustomResult.Failure(DataError.Remote.REQUEST_TIMEOUT)
        409 -> CustomResult.Failure(DataError.Remote.CONFLICT)
        413 -> CustomResult.Failure(DataError.Remote.PAYLOAD_TOO_LARGE)
        429 -> CustomResult.Failure(DataError.Remote.TOO_MANY_REQUESTS)
        500 -> CustomResult.Failure(DataError.Remote.INTERNAL_SERVER_ERROR)
        503 -> CustomResult.Failure(DataError.Remote.SERVICE_UNAVAILABLE)
        else -> CustomResult.Failure(DataError.Remote.UNKNOWN)
    }
}

expect suspend fun <T> platformSafeCall(
    execute: suspend () -> HttpResponse,
    handleResponse: suspend (HttpResponse) -> CustomResult<T, DataError.Remote>
): CustomResult<T, DataError.Remote>

suspend inline fun <reified T> safeCall(
    noinline execute: suspend () -> HttpResponse
): CustomResult<T, DataError.Remote> {
    return platformSafeCall(
        execute = execute,
    ) {
        responseToResult(it)
    }
}