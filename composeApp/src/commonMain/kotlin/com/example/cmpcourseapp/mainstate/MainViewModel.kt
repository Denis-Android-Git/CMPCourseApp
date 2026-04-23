package com.example.cmpcourseapp.mainstate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.util.PlatformUtils
import com.example.domain.auth.SessionStorage
import com.example.domain.notification.DeviceTokenService
import com.example.domain.notification.PushNotificationService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionStorage: SessionStorage,
    private val pushNotificationService: PushNotificationService,
    private val deviceTokenService: DeviceTokenService
) : ViewModel() {

    private val sessionExpiredChannel = Channel<MainEvent>()
    val sessionExpiredFlow = sessionExpiredChannel.receiveAsFlow()
    private val _mainState = MutableStateFlow(MainState())
    private var hasLoadedInitialData = false
    val mainState = _mainState
        .onStart {
            if (!hasLoadedInitialData) {
                observeSession()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MainState()
        )

    private var previousToken: String? = null
    private var currentDeviceToken: String? = null
    private var previousDeviceToken: String? = null

    init {
        viewModelScope.launch {
            val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
            _mainState.update {
                it.copy(
                    isCheckingAuthStatus = false,
                    isLoggedIn = authInfo != null
                )
            }
        }
    }

    private fun observeSession() {
        sessionStorage
            .observeAuthInfo()
            .onEach { authInfo ->
                val currentToken = authInfo?.refreshToken
                val isSessionExpired = previousToken != null && currentToken == null
                if (isSessionExpired) {
                    sessionStorage.set(null)
                    _mainState.update {
                        it.copy(
                            isLoggedIn = false

                        )
                    }
                    currentDeviceToken?.let {
                        deviceTokenService.unregisterToken(it)
                    }
                    sessionExpiredChannel.send(MainEvent.SessionExpired)
                }
                previousToken = currentToken
            }
            .combine(
                pushNotificationService.observeDeviceToken()
            ) { authInfo, deviceToken ->
                if (authInfo != null && deviceToken != previousDeviceToken && deviceToken != null) {
                    currentDeviceToken = deviceToken
                    registerDeviceToken(deviceToken, PlatformUtils.getOsName())
                    previousDeviceToken = deviceToken
                }
            }
            .launchIn(viewModelScope)

    }

    private fun registerDeviceToken(token: String, platform: String) {
        viewModelScope.launch {
            deviceTokenService.registerToken(token, platform)
        }
    }

}