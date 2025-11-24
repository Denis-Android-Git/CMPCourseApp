package com.example.presentation.chat_list

import com.example.domain.auth.AuthInfo

data class ChatListScreenState(
    val accessToken: String = "accessToken",
    val refreshToken: String = "refreshToken",
    val id: String = "id",
    val userName: String = "userName",
    val email: String = "email",
    val hasVarifiedEmail: Boolean = false,
    val authInfo: AuthInfo? = null,
    val encryptedString: String = "encryptedString"
)