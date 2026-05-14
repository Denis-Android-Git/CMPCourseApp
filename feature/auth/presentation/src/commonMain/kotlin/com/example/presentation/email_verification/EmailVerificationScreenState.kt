package com.example.presentation.email_verification

import androidx.compose.foundation.text.input.TextFieldState

data class EmailVerificationScreenState(
    val isVarifying: Boolean = false,
    val isEmailVerified: Boolean = false,
    val isTokenExpired: Boolean = false,
    val emailTextFieldState: TextFieldState = TextFieldState(),
    val canResendEmail: Boolean = false,
)