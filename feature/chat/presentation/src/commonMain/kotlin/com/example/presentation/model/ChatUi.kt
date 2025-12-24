package com.example.presentation.model

import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.domain.models.ChatMessage

data class ChatUi(
    val id: String,
    val localParticipant: ChatParticipantUi,
    val remoteParticipants: List<ChatParticipantUi>,
    val lastMessage: ChatMessage?,
    val lastMessageSenderName: String?

)
