package com.example.presentation.forgot_password

import androidx.compose.foundation.text.input.TextFieldState
import com.example.presentation.util.UiText

data class ForgotPasswordState(
    val emailTextFieldState: TextFieldState = TextFieldState(),
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val isEmailSent: Boolean = false,
    val canSubmit: Boolean = false
)