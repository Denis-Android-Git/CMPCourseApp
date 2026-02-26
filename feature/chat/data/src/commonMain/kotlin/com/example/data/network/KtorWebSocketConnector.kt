package com.example.data.network

import com.example.data.dto.ws.WsMessageDto
import com.example.data.lifeCycle.AppLifeCycleObserver
import com.example.data.logging.KermitLogger
import com.example.domain.auth.SessionStorage
import com.example.domain.error.ConnectionError
import com.example.domain.logging.MyLogger
import com.example.domain.models.ConnectionState
import com.example.domain.util.CustomResult
import com.example.domain.util.EmptyResult
import com.example.feature.chat.data.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

class KtorWebSocketConnector(
    private val httpClient: HttpClient,
    applicationScope: CoroutineScope,
    sessionStorage: SessionStorage,
    private val json: Json,
    private val connectionErrorHandler: ConnectionErrorHandler,
    private val connectionRetryHandler: ConnectionRetryHandler,
    appLifeCycleObserver: AppLifeCycleObserver,
    connectivityObserver: ConnectivityObserver,
    private val myLogger: MyLogger = KermitLogger
) {
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()
    private var currentSession: WebSocketSession? = null

    @OptIn(FlowPreview::class)
    private val isConnected = connectivityObserver
        .isConnected
        .debounce(1.seconds)
        .stateIn(
            applicationScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )
    private val isInForeground = appLifeCycleObserver
        .isInForeground
        .onEach {
            if (it) {
                connectionRetryHandler.resetDelay()
            }
        }
        .stateIn(
            applicationScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val messages = combine(
        sessionStorage.observeAuthInfo(),
        isConnected,
        isInForeground
    ) { info, isConnected, isInForeground ->
        when {
            info == null -> {
                myLogger.info("KtorWebSocketConnector observeAuthInfo is null")
                _connectionState.update { ConnectionState.DISCONNECTED }
                currentSession?.close()
                currentSession = null
                connectionRetryHandler.resetDelay()
                null
            }

            !isInForeground -> {
                myLogger.info("KtorWebSocketConnector isInForeground is false")
                _connectionState.update { ConnectionState.DISCONNECTED }
                currentSession?.close()
                currentSession = null
                null
            }

            !isConnected -> {
                myLogger.info("KtorWebSocketConnector isConnected is false")
                _connectionState.update { ConnectionState.ERROR_NETWORK }
                currentSession?.close()
                currentSession = null
                null
            }

            else -> {
                myLogger.info("KtorWebSocketConnector isConnected is true")
                if (_connectionState.value !in listOf(
                        ConnectionState.CONNECTING,
                        ConnectionState.CONNECTED
                    )
                ) {
                    _connectionState.update { ConnectionState.CONNECTING }
                }
                info
            }
        }
    }
        .flatMapLatest { info ->
            info?.let {
                createWebSocketFlow(info.accessToken)
                    .catch { exception ->
                        myLogger.error("KtorWebSocketConnector WebSocket flow error", exception)
                        currentSession?.close()
                        currentSession = null
                        val transformedException =
                            connectionErrorHandler.transformException(exception)
                        throw transformedException
                    }
                    .retryWhen { cause, attempt ->
                        myLogger.info("KtorWebSocketConnector retryWhen attempt: $attempt, cause: ${cause.message}")
                        val shouldRetry = connectionRetryHandler.shouldRetry(cause, attempt)
                        if (shouldRetry) {
                            _connectionState.update { ConnectionState.CONNECTING }
                            connectionRetryHandler.applyRetryDelay(attempt)
                        }
                        shouldRetry
                    }
                    .catch { exception ->
                        //for non retryable errors
                        myLogger.error("KtorWebSocketConnector secondary catch error", exception)
                        _connectionState.update {
                            connectionErrorHandler.getConnectionStateFromError(
                                exception
                            )
                        }
                    }
            } ?: emptyFlow()
        }

    private fun createWebSocketFlow(accessToken: String) = callbackFlow {
        _connectionState.update { ConnectionState.CONNECTING }
        currentSession = httpClient.webSocketSession(
            urlString = "${UrlConstant.BASE_URL_WS}/chat"
        ) {
            header("Authorization", "Bearer $accessToken")
            header("X-API-KEY", BuildKonfig.API_KEY)
        }
        currentSession?.let { session ->
            _connectionState.update { ConnectionState.CONNECTED }

            session
                .incoming
                .consumeAsFlow()
                .buffer(
                    capacity = 100
                )
                .collect { frame ->
                    when (frame) {
                        is Frame.Text -> {
                            val message = frame.readText()
                            myLogger.info("KtorWebSocketConnector Received message: $message")
                            val messageDto = json.decodeFromString<WsMessageDto>(message)
                            send(messageDto)
                        }

                        is Frame.Ping -> {
                            myLogger.debug("KtorWebSocketConnector Received ping")
                            session.send(Frame.Pong(frame.data))
                        }

                        else -> Unit
                    }
                }

        } ?: throw Exception("Failed to create WebSocket session")
        awaitClose {
            launch {
                withContext(NonCancellable) {
                    myLogger.info("KtorWebSocketConnector WebSocket session closed")
                    _connectionState.update { ConnectionState.DISCONNECTED }
                    currentSession?.close()
                    currentSession = null
                }
            }
        }
    }

    suspend fun sendMessage(message: String): EmptyResult<ConnectionError> {
        val connectionState = connectionState.value
        if (currentSession == null || connectionState != ConnectionState.CONNECTED) {
            return CustomResult.Failure(ConnectionError.NOT_CONNECTED)
        }
        return try {
            currentSession?.send(message)
            CustomResult.Success(Unit)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            myLogger.error("KtorWebSocketConnector sendMessage error", e)
            CustomResult.Failure(ConnectionError.MESSAGE_SEND_FAILED)
        }

    }
}
