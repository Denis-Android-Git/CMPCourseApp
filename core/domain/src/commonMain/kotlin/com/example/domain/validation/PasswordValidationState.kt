package com.example.domain.validation

data class PasswordValidationState(
    val hasUppercase: Boolean = false,
    val hasNumber: Boolean = false,
    val hasMinLength: Boolean = false
) {
    val isValidPassword: Boolean
        get() = hasUppercase && hasNumber && hasMinLength
}
