package com.example.presentation.email_verification

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.EmailValidator
import com.example.domain.auth.AuthService
import com.example.domain.logging.MyLogger
import com.example.domain.util.DataError
import com.example.domain.util.onFailure
import com.example.domain.util.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmailVerificationScreenViewModel(
    private val authService: AuthService,
    savedStateHandle: SavedStateHandle,
    private val myLogger: MyLogger
) : ViewModel() {


    private val token = savedStateHandle.get<String>("token")
    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(EmailVerificationScreenState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                myLogger.debug("uri_check = EmailVerificationScreenViewModel: $token")
                verifyEmail()
                observeEmail()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = EmailVerificationScreenState()
        )
    private val isEmailValidFlow = snapshotFlow { state.value.emailTextFieldState.text.toString() }
        .map { email -> EmailValidator.validateEmail(email) }
        .distinctUntilChanged()

    //actions are only for navigation
    fun onAction(action: EmailVerificationScreenAction) = Unit


    private fun observeEmail() {
        isEmailValidFlow.onEach { isEmailValid ->
            _state.update {
                it.copy(
                    canResendEmail = isEmailValid
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun verifyEmail() {
        viewModelScope.launch {
            _state.update {
                it.copy(isVarifying = true)
            }
            authService.verifyEmail(token ?: "Invalid Token")
                .onSuccess {
                    _state.update {
                        it.copy(
                            isVarifying = false,
                            isEmailVerified = true,
                            isTokenExpired = false
                        )
                    }
                }
                .onFailure { dataErrorRemote ->
                    when (dataErrorRemote) {
                        DataError.Remote.TOKEN_EXPIRED -> {
                            myLogger.info("EmailVerificationScreenViewModel_onFailure: $dataErrorRemote")
                            _state.update {
                                it.copy(
                                    isVarifying = false,
                                    isEmailVerified = false,
                                    isTokenExpired = true
                                )
                            }
                        }

                        else -> {
                            myLogger.info("EmailVerificationScreenViewModel_onFailure: $dataErrorRemote")
                            _state.update {
                                it.copy(
                                    isVarifying = false,
                                    isEmailVerified = false,
                                    isTokenExpired = false
                                )
                            }
                        }
                    }
                }
        }
    }

}