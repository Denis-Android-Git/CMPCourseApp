package com.example.presentation.reset_password

sealed interface ResetPasswordAction {
    data object OnSubmitClick : ResetPasswordAction
    data object OnVisibilityClick : ResetPasswordAction
}