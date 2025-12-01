package com.example.presentation.reset_password

import androidx.compose.foundation.text.input.TextFieldState
import com.example.presentation.util.UiText

data class ResetPasswordState(
    val passwordState: TextFieldState = TextFieldState(),
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val isPasswordVisible: Boolean = false,
    val canSubmit: Boolean = false,
    val isSuccess: Boolean = false
)