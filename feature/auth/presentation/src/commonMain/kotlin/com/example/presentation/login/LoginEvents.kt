package com.example.presentation.login

sealed interface LoginEvents {
    data object Success : LoginEvents
    data class Failure(
        val email: String,
        val isNeedToResendVerification: Boolean = false
    ) : LoginEvents
}