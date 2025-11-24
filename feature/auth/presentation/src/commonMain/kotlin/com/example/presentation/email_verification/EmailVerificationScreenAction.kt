package com.example.presentation.email_verification

sealed interface EmailVerificationScreenAction {
    data object OnLoginClick : EmailVerificationScreenAction
    data object OnCloseClick : EmailVerificationScreenAction
}