package com.example.data.network

import com.example.core.data.BuildKonfig
import com.example.data.auth.dto.AuthInfoSerializable
import com.example.data.auth.dto.requests.RefreshRequest
import com.example.data.mappers.toDomain
import com.example.domain.auth.SessionStorage
import com.example.domain.logging.MyLogger
import com.example.domain.util.onFailure
import com.example.domain.util.onSuccess
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json

class HttpClientFactory(
    private val myLogger: MyLogger,
    private val sessionStorage: SessionStorage
) {
    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(HttpTimeout) {
                connectTimeoutMillis = 20000
                requestTimeoutMillis = 20000
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        myLogger.debug(message)
                    }
                }
                level = LogLevel.ALL
            }
            install(WebSockets) {
                pingIntervalMillis = 20000
            }
            defaultRequest {
                header("x-api-key", BuildKonfig.API_KEY)
                contentType(ContentType.Application.Json)
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        sessionStorage.observeAuthInfo()
                            .firstOrNull()
                            ?.let {
                                BearerTokens(
                                    accessToken = it.accessToken,
                                    refreshToken = it.refreshToken
                                )
                            }
                    }
                    refreshTokens {
                        if (response.request.url.encodedPath.contains("auth/")) {
                            return@refreshTokens null
                        }
                        val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
                        if (authInfo?.refreshToken.isNullOrBlank()) {
                            sessionStorage.set(null)
                            return@refreshTokens null
                        }
                        var bearerTokens: BearerTokens? = null
                        client.post<RefreshRequest, AuthInfoSerializable>(
                            route = "/auth/refresh",
                            body = RefreshRequest(authInfo.refreshToken),
                            builder = {
                                markAsRefreshTokenRequest() //to break infinite refresh loop
                            }
                        )
                            .onSuccess { newAuthInfo ->
                                sessionStorage.set(newAuthInfo.toDomain())
                                bearerTokens = BearerTokens(
                                    accessToken = newAuthInfo.accessToken,
                                    refreshToken = newAuthInfo.refreshToken
                                )
                            }
                            .onFailure {
                                sessionStorage.set(null)
                            }
                        bearerTokens
                    }
                }
            }
        }
    }
}