package com.example.data.mappers

import com.example.data.dto.ChatDto
import com.example.data.dto.ChatMessageDto
import com.example.data.dto.ChatParticipantDto
import com.example.data.dto.ws.OutgoingWsDto
import com.example.database.entities.ChatEntity
import com.example.database.entities.ChatInfoEntity
import com.example.database.entities.ChatMessageEntity
import com.example.database.entities.ChatParticipantEntity
import com.example.database.entities.ChatWithParticipants
import com.example.database.view.LastMessageView
import com.example.domain.models.Chat
import com.example.domain.models.ChatInfo
import com.example.domain.models.ChatMessage
import com.example.domain.models.ChatParticipant
import com.example.domain.models.DeliveryStatus
import com.example.domain.models.MessageWithSender
import kotlin.time.Instant

typealias DataMessageWithSender = com.example.database.entities.MessageWithSender
typealias DomainMessageWithSender = MessageWithSender


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
    senderId = senderId,
    deliveryStatus = DeliveryStatus.SENT
)

fun ChatDto.toDomain() = Chat(
    id = id,
    memberList = participants.map { it.toDomain() },
    lastActivityAt = Instant.parse(lastActivityAt),
    lastMessage = lastMessage?.toDomain()
)

fun ChatWithParticipants.toDomain(): Chat {
    return Chat(
        id = chat.chatId,
        memberList = participants.map { it.toDomain() },
        lastActivityAt = Instant.fromEpochMilliseconds(chat.lastActivityAt),
        lastMessage = lastMessageView?.toDomain()
    )
}

fun ChatParticipantEntity.toDomain() = ChatParticipant(
    userId = userId,
    userName = userName,
    profilePictureUrl = profilePictureUrl
)

fun LastMessageView.toDomain() = ChatMessage(
    id = messageId,
    chatId = chatId,
    content = content,
    senderId = senderId,
    createdAt = Instant.fromEpochMilliseconds(timeStamp),
    deliveryStatus = DeliveryStatus.valueOf(deliveryStatus)
)

fun ChatParticipant.toEntity() = ChatParticipantEntity(
    userId = userId,
    userName = userName,
    profilePictureUrl = profilePictureUrl
)

fun ChatMessage.toEntity() = ChatMessageEntity(
    messageId = id,
    chatId = chatId,
    senderId = senderId,
    content = content,
    timeStamp = createdAt.toEpochMilliseconds(),
    deliveryStatus = deliveryStatus.name
)

fun ChatMessage.toNewMessage() = OutgoingWsDto.NewMessage(
    messageId = id,
    chatId = chatId,
    content = content
)

fun ChatMessage.toLastMessageView() = LastMessageView(
    messageId = id,
    chatId = chatId,
    senderId = senderId,
    content = content,
    timeStamp = createdAt.toEpochMilliseconds(),
    deliveryStatus = deliveryStatus.name
)

fun Chat.toEntity() = ChatEntity(
    chatId = id,
    lastActivityAt = lastActivityAt.toEpochMilliseconds()
)

fun ChatEntity.toDomain(
    participants: List<ChatParticipant>,
    lastMessage: ChatMessage? = null
) = Chat(
    id = chatId,
    memberList = participants,
    lastActivityAt = Instant.fromEpochMilliseconds(lastActivityAt),
    lastMessage = lastMessage
)

fun ChatMessageEntity.toDomain() = ChatMessage(
    id = chatId,
    chatId = chatId,
    content = content,
    createdAt = Instant.fromEpochMilliseconds(timeStamp),
    senderId = senderId,
    deliveryStatus = DeliveryStatus.SENT
)

fun DataMessageWithSender.toDomain() = DomainMessageWithSender(
    message = message.toDomain(),
    sender = sender.toDomain(),
    deliveryStatus = DeliveryStatus.valueOf(this.message.deliveryStatus)
)

fun ChatInfoEntity.toDomain() = ChatInfo(
    chat = chat.toDomain(
        participants = this.participants.map { it.toDomain() }
    ),
    messages = messagesWithSenders.map { it.toDomain() }
)

