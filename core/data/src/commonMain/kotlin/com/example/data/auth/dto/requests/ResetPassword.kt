package com.example.data.auth.dto.requests

import kotlinx.serialization.Serializable

@Serializable
data class ResetPassword(
    val token: String,
    val newPassword: String
)
