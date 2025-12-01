package com.example.presentation.reset_password

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cmpcourseapp.feature.auth.presentation.generated.resources.Res
import cmpcourseapp.feature.auth.presentation.generated.resources.error_reset_password_token_invalid
import cmpcourseapp.feature.auth.presentation.generated.resources.error_same_password
import com.example.domain.auth.AuthService
import com.example.domain.util.DataError
import com.example.domain.util.onFailure
import com.example.domain.util.onSuccess
import com.example.domain.validation.PasswordValidator
import com.example.presentation.util.UiText
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

class ResetPasswordViewModel(
    private val authService: AuthService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val token = savedStateHandle.get<String>("token") ?: ""
    private val _state = MutableStateFlow(ResetPasswordState())
    private val isPasswordValidFlow = snapshotFlow { state.value.passwordState.text.toString() }
        .map { password -> PasswordValidator.validate(password).isValidPassword }
        .distinctUntilChanged()

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
            initialValue = ResetPasswordState()
        )

    fun onAction(action: ResetPasswordAction) {
        when (action) {
            ResetPasswordAction.OnSubmitClick -> resetPassword()
            ResetPasswordAction.OnVisibilityClick -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }
        }
    }

    private fun resetPassword() {
        if (state.value.isLoading || !state.value.canSubmit) return
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, isPasswordVisible = false)
            }
            val newPassword = state.value.passwordState.text.toString()
            authService.resetPassword(token, password = newPassword)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            error = null
                        )
                    }
                }
                .onFailure { dataErrorRemote ->
                    val errorText = when (dataErrorRemote) {
                        DataError.Remote.UNAUTHORIZED -> UiText.MyStringResource(Res.string.error_reset_password_token_invalid)
                        DataError.Remote.CONFLICT -> UiText.MyStringResource(Res.string.error_same_password)

                        else -> dataErrorRemote.toUiText()
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = errorText
                        )
                    }
                }
        }
    }

    private fun observeValidation() {
        isPasswordValidFlow.onEach { isPasswordValid ->
            _state.update {
                it.copy(canSubmit = isPasswordValid)
            }
        }.launchIn(viewModelScope)
    }
}