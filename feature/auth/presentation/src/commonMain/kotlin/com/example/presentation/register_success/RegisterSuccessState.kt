package com.example.presentation.register_success

import com.example.presentation.util.UiText

data class RegisterSuccessState(
    val registeredEmail: String = "",
    val isResendingEmail: Boolean = false,
    val resendVarificationError: UiText? = null
)