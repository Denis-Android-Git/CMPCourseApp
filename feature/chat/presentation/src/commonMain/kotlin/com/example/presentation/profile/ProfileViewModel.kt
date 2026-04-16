package com.example.presentation.profile

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.error_current_password_equal_to_new_one
import cmpcourseapp.feature.chat.presentation.generated.resources.error_current_password_incorrect
import cmpcourseapp.feature.chat.presentation.generated.resources.error_invalid_file_type
import com.example.domain.auth.AuthService
import com.example.domain.auth.SessionStorage
import com.example.domain.participant.ChatParticipantRepository
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
    private val authService: AuthService,
    private val chatParticipantRepository: ChatParticipantRepository,
    sessionStorage: SessionStorage
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ProfileState())
    val state = combine(
        _state,
        sessionStorage.observeAuthInfo()
    ) { currentState, authInfo ->
        if (authInfo != null) {
            currentState.copy(
                userName = authInfo.user.userName,
                emailTextState = TextFieldState(initialText = authInfo.user.email),
                profilePicture = authInfo.user.profilePicture
            )
        } else currentState
    }
        .onStart {
            if (!hasLoadedInitialData) {
                observeCanChangePassword()
                fetchLocalUser()
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
            is ProfileAction.OnImageSelected -> uploadProfilePicture(action.bytes, action.mimeType)
            else -> Unit
        }
    }

    private fun uploadProfilePicture(bytes: ByteArray, mimeType: String?) {
        if (state.value.isUploadingImage) return
        if (mimeType == null) {
            _state.update { it.copy(imageError = UiText.MyStringResource(Res.string.error_invalid_file_type)) }
            return
        }

        _state.update { it.copy(isUploadingImage = true, imageError = null) }

        viewModelScope.launch {
            chatParticipantRepository.uploadProfilePicture(
                bytes = bytes,
                mimeType = mimeType
            )
                .onSuccess {
                    _state.update { it.copy(isUploadingImage = false) }

                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isUploadingImage = false,
                            imageError = error.toUiText()
                        )
                    }

                }
        }
    }

    private fun fetchLocalUser() {
        viewModelScope.launch {
            chatParticipantRepository.fetchLocalUser()
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
            snapshotFlow { _state.value.currentPasswordTextState.text.toString() }.map { it.isNotBlank() }
                .distinctUntilChanged()
        val isNewPasswordValidFlow =
            snapshotFlow { _state.value.newPasswordTextState.text.toString() }.map {
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