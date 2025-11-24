package com.example.domain.auth

data class User(
    val id: String,
    val userName: String,
    val email: String,
    val hasVarifiedEmail: Boolean,
    val profilePicture: String? = null
)
