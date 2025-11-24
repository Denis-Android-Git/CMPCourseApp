package com.example.presentation.login

sealed interface LoginEvents {
    data object Success : LoginEvents
}