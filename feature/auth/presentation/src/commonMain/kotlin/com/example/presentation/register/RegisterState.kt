package com.example.presentation.register

import androidx.compose.foundation.text.input.TextFieldState
import com.example.presentation.util.UiText

data class RegisterState(
    val emailTextState: TextFieldState = TextFieldState(),
    val isEmailValid: Boolean = false,
    val emailError: UiText? = null,
    val passwordTextState: TextFieldState = TextFieldState(),
    val isPasswordValid: Boolean = false,
    val passwordError: UiText? = null,
    val userNameTextState: TextFieldState = TextFieldState(),
    val isUserNameValid: Boolean = false,
    val userNameError: UiText? = null,
    val registerError: UiText? = null,
    val isRegistering: Boolean = false,
    val canRegister: Boolean = false,
    val isPasswordVisible: Boolean = false
)