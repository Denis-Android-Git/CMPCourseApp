package com.example.presentation.mappers

import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.domain.auth.User
import com.example.domain.models.Chat
import com.example.domain.models.ChatParticipant
import com.example.presentation.model.ChatUi

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