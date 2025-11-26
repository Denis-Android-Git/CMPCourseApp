package com.example.cmpcourseapp.mainstate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.auth.SessionStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionStorage: SessionStorage
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
                    sessionExpiredChannel.send(MainEvent.SessionExpired)
                }
                previousToken = currentToken
            }
            .launchIn(viewModelScope)

    }

}