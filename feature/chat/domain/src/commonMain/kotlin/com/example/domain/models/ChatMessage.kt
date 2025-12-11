package com.example.domain.models

import kotlin.time.Instant

data class ChatMessage(
    val content: String,
    val id: String,
    val chatId: String,
    val createdAt: Instant,
    val senderId: String
)
