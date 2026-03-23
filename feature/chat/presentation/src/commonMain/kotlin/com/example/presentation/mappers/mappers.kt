package com.example.presentation.mappers

import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.domain.auth.User
import com.example.domain.models.Chat
import com.example.domain.models.ChatParticipant
import com.example.domain.models.MessageWithSender
import com.example.presentation.model.ChatUi
import com.example.presentation.model.MessageUi
import com.example.presentation.util.DateUtil

fun ChatParticipant.toUi() = ChatParticipantUi(
    id = userId,
    imageUrl = profilePictureUrl,
    name = userName,
    initials = initials
)

fun User.toUi() = ChatParticipantUi(
    id = id,
    name = userName,
    initials = userName.take(2).uppercase()
)

fun MessageWithSender.toUi(
    localUserId: String
): MessageUi {
    val isFromLocalUser = this.sender.userId == localUserId
    return if (isFromLocalUser) {
        MessageUi.LocalUserMessage(
            id = message.id,
            content = message.content,
            deliveryStatus = message.deliveryStatus,
            formattedSentTime = DateUtil.formatMessageTime(message.createdAt),
        )
    } else {
        MessageUi.OtherUserMessage(
            id = message.id,
            content = message.content,
            formattedSentTime = DateUtil.formatMessageTime(message.createdAt),
            sender = sender.toUi()
        )
    }
}

fun Chat.toUi(localParticipantId: String): ChatUi {
    val (local, other) = memberList.partition { it.userId == localParticipantId }
    return ChatUi(
        id = id,
        localParticipant = local.first().toUi(),
        remoteParticipants = other.map { it.toUi() },
        lastMessage = lastMessage,
        lastMessageSenderName = memberList
            .find {
                it.userId == lastMessage?.senderId
            }
            ?.userName
    )
}