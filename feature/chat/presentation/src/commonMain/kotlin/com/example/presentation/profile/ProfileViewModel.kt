package com.example.presentation.profile

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.error_current_password_equal_to_new_one
import cmpcourseapp.feature.chat.presentation.generated.resources.error_current_password_incorrect
import com.example.domain.auth.AuthService
import com.example.domain.util.DataError
import com.example.domain.util.onFailure
import com.example.domain.util.onSuccess
import com.example.domain.validation.PasswordValidator
import com.example.presentation.util.UiText
import com.example.presentation.util.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authService: AuthService
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ProfileState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeCanChangePassword()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileState()
        )

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.OnChangePasswordClick -> changePassword()
            is ProfileAction.OnToggleCurrentPasswordVisibility -> toggleCurrentPasswordVisibility()
            is ProfileAction.OnToggleNewPasswordVisibility -> toggleNewPasswordVisibility()
            else -> Unit
        }
    }

    private fun toggleNewPasswordVisibility() {
        _state.update {
            it.copy(
                isNewPasswordVisible = !it.isNewPasswordVisible
            )
        }
    }

    private fun toggleCurrentPasswordVisibility() {
        _state.update {
            it.copy(
                isCurrentPasswordVisible = !it.isCurrentPasswordVisible
            )
        }
    }

    private fun changePassword() {
        if (!state.value.canChangePassword && state.value.isChangingPassword) return
        _state.update {
            it.copy(
                isChangingPassword = true,
                isPasswordChangeSuccessful = false
            )
        }
        viewModelScope.launch {
            authService.changePassword(
                oldPassword = state.value.currentPasswordTextState.text.toString(),
                newPassword = state.value.newPasswordTextState.text.toString()
            )
                .onSuccess {
                    state.value.currentPasswordTextState.clearText()
                    state.value.newPasswordTextState.clearText()
                    _state.update {
                        it.copy(
                            isChangingPassword = false,
                            newPasswordError = null,
                            isNewPasswordVisible = false,
                            isCurrentPasswordVisible = false,
                            isPasswordChangeSuccessful = true
                        )
                    }
                }
                .onFailure { error ->
                    val errorMessage = when (error) {
                        DataError.Remote.UNAUTHORIZED -> UiText.MyStringResource(Res.string.error_current_password_incorrect)
                        DataError.Remote.CONFLICT -> UiText.MyStringResource(Res.string.error_current_password_equal_to_new_one)
                        else -> error.toUiText()
                    }
                    _state.update {
                        it.copy(
                            isChangingPassword = false,
                            newPasswordError = errorMessage
                        )
                    }
                }
        }
    }

    private fun observeCanChangePassword() {
        val isCurrentPasswordValidFlow =
            snapshotFlow { state.value.currentPasswordTextState.text.toString() }.map { it.isNotBlank() }
                .distinctUntilChanged()
        val isNewPasswordValidFlow =
            snapshotFlow { state.value.newPasswordTextState.text.toString() }.map {
                PasswordValidator.validate(it).isValidPassword
            }.distinctUntilChanged()

        combine(
            isCurrentPasswordValidFlow,
            isNewPasswordValidFlow
        ) { currentPasswordValid, newPasswordValid ->
            _state.update {
                it.copy(
                    canChangePassword = currentPasswordValid && newPasswordValid
                )
            }
        }.launchIn(viewModelScope)
    }
}