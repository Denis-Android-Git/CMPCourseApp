package com.example.data.mappers

import com.example.data.dto.ChatDto
import com.example.data.dto.ChatMessageDto
import com.example.data.dto.ChatParticipantDto
import com.example.domain.models.Chat
import com.example.domain.models.ChatMessage
import com.example.domain.models.ChatParticipant
import kotlin.time.Instant

fun ChatParticipantDto.toDomain() = ChatParticipant(
    userId = userId,
    userName = username,
    profilePictureUrl = profilePictureUrl
)

fun ChatMessageDto.toDomain() = ChatMessage(
    id = id,
    chatId = chatId,
    content = content,
    createdAt = Instant.parse(createdAt),
    senderId = senderId
)

fun ChatDto.toDomain() = Chat(
    id = id,
    memberList = participants.map { it.toDomain() },
    lastActivityAt = Instant.parse(lastActivityAt),
    lastMessage = lastMessage?.toDomain()
)