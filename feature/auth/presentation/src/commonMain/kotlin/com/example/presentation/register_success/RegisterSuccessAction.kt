package com.example.presentation.register_success

sealed interface RegisterSuccessAction {
    data object OnLoginClick : RegisterSuccessAction
    data object OnResendVerificationEmailClick : RegisterSuccessAction
}