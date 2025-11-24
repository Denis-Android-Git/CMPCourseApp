package com.example.presentation.login

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cmpcourseapp.feature.auth.presentation.generated.resources.Res
import cmpcourseapp.feature.auth.presentation.generated.resources.error_email_not_verified
import cmpcourseapp.feature.auth.presentation.generated.resources.error_invalid_credentials
import com.example.domain.EmailValidator
import com.example.domain.auth.AuthService
import com.example.domain.auth.SessionStorage
import com.example.domain.logging.MyLogger
import com.example.domain.util.DataError
import com.example.domain.util.onFailure
import com.example.domain.util.onSuccess
import com.example.presentation.util.UiText
import com.example.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authService: AuthService,
    private val sessionStorage: SessionStorage,
    private val myLogger: MyLogger
) : ViewModel() {

    private val eventChannel = Channel<LoginEvents>()
    val eventsFlow = eventChannel.receiveAsFlow()
    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(LoginState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeTextStates()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = LoginState()
        )
    private val isEmailValidFlow = snapshotFlow { state.value.emailTextFieldState.text.toString() }
        .map { email -> EmailValidator.validateEmail(email) }
        .distinctUntilChanged()

    private val isPasswordNotBlankFlow = snapshotFlow { state.value.passwordTextFieldState.text.toString() }
        .map { password -> password.isNotBlank() }
        .distinctUntilChanged()
    private val isLoggingIn = state
        .map { it.isLoggingIn }
        .distinctUntilChanged()

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.OnLoginClick -> login()
            LoginAction.OnTogglePasswordVisibility -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }

            else -> Unit

        }
    }

    private fun observeTextStates() {
        combine(
            isEmailValidFlow,
            isPasswordNotBlankFlow,
            isLoggingIn
        ) { isEmailValid, isPasswordNotBlank, isLoggingIn ->

            myLogger.debug("checking_canLogin isEmailValid: $isEmailValid, isPasswordNotBlank: $isPasswordNotBlank, isLoggingIn: $isLoggingIn")

            _state.update {
                it.copy(
                    canLogin = isEmailValid && isPasswordNotBlank && !isLoggingIn
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun login() {
        if (!state.value.canLogin) {
            return
        }
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoggingIn = true
                )
            }
            val email = state.value.emailTextFieldState.text.toString()
            val password = state.value.passwordTextFieldState.text.toString()
            authService.login(
                email = email,
                password = password
            )
                .onSuccess { authInfo ->
                    sessionStorage.set(authInfo)
                    eventChannel.send(LoginEvents.Success)
                    _state.update {
                        it.copy(
                            isLoggingIn = false
                        )
                    }
                }
                .onFailure { error ->
                    val errorMessage = when (error) {
                        DataError.Remote.UNAUTHORIZED -> UiText.MyStringResource(Res.string.error_invalid_credentials)
                        DataError.Remote.FORBIDDEN -> UiText.MyStringResource(Res.string.error_email_not_verified)
                        else -> error.toUiText()
                    }
                    _state.update {
                        it.copy(
                            isLoggingIn = false,
                            error = errorMessage
                        )
                    }
                }
        }
    }

}