package com.example.domain

object EmailValidator {
    private const val EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    fun validateEmail(email: String): Boolean {
        return EMAIL_PATTERN.toRegex().matches(email)
    }
}