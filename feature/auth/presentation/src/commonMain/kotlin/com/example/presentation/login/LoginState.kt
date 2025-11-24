package com.example.presentation.login

import androidx.compose.foundation.text.input.TextFieldState
import com.example.presentation.util.UiText

data class LoginState(
    val emailTextFieldState: TextFieldState = TextFieldState(),
    val passwordTextFieldState: TextFieldState = TextFieldState(),
    val isLoggingIn: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val canLogin: Boolean = false,
    val error: UiText? = null
)