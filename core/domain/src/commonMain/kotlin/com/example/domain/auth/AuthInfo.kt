package com.example.domain.auth

data class AuthInfo(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)
