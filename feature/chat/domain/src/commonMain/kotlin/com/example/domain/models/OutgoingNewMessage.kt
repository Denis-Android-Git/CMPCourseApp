package com.example.domain.models

data class OutgoingNewMessage(
    val chatId: String,
    val messageId: String,
    val content: String
)