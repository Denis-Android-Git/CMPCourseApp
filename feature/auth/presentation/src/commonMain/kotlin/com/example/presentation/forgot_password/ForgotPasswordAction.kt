package com.example.presentation.forgot_password

sealed interface ForgotPasswordAction {
    data object OnSubmitClick : ForgotPasswordAction
}