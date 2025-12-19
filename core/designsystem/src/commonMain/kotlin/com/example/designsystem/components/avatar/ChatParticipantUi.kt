package com.example.designsystem.components.avatar

data class ChatParticipantUi(
    val id: String,
    val imageUrl: String? = null,
    val name: String,
    val initials: String
)
