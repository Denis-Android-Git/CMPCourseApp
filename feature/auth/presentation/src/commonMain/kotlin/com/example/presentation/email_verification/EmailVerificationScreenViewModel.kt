package com.example.presentation.email_verification

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.auth.AuthService
import com.example.domain.logging.MyLogger
import com.example.domain.util.onFailure
import com.example.domain.util.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = EmailVerificationScreenState()
        )

    //actions are only for navigation
    fun onAction(action: EmailVerificationScreenAction) = Unit

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
                            isEmailVerified = true
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isVarifying = false,
                            isEmailVerified = false
                        )
                    }
                }
        }
    }

}