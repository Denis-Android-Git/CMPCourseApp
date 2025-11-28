package com.example.presentation.forgot_password

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.EmailValidator
import com.example.domain.auth.AuthService
import com.example.domain.util.onFailure
import com.example.domain.util.onSuccess
import com.example.presentation.util.toUiText
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

class ForgotPasswordViewModel(
    private val authService: AuthService
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeValidation()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ForgotPasswordState()
        )

    private val isEmailValidFlow = snapshotFlow { state.value.emailTextFieldState.text.toString() }
        .map { email -> EmailValidator.validateEmail(email) }
        .distinctUntilChanged()

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.OnSubmitClick -> forgotPassword()
        }
    }

    private fun observeValidation() {
        isEmailValidFlow.onEach { isEmailValid ->
            _state.update {
                it.copy(
                    canSubmit = isEmailValid
                )
            }
        }
            .launchIn(viewModelScope)
    }

    private fun forgotPassword() {
        if (state.value.isLoading || !state.value.canSubmit) {
            return
        }
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, isEmailSent = false, error = null)
            }
            val email = state.value.emailTextFieldState.text.toString()
            authService.forgotPassword(email)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isEmailSent = true
                        )
                    }
                }
                .onFailure { dataErrorRemote ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = dataErrorRemote.toUiText()
                        )
                    }
                }

        }
    }

}