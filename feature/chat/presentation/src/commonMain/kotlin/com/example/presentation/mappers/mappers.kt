package com.example.presentation.mappers

import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.domain.auth.User
import com.example.domain.models.Chat
import com.example.domain.models.ChatParticipant
import com.example.domain.models.MessageWithSender
import com.example.presentation.model.ChatUi
import com.example.presentation.model.MessageUi
import com.example.presentation.util.DateUtil
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


fun List<MessageWithSender>.toUiList(localUserId: String): List<MessageUi> {
    return this
        .sortedByDescending {
            it.message.createdAt
        }
        .groupBy {
            it.message.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).date
        }
        .flatMap { (date, messages) ->
            messages.map { it.toUi(localUserId) } + MessageUi.DateSeparator(
                id = date.toString(),
                date = DateUtil.formatDateSeparator(date)
            )
        }

}

fun ChatParticipant.toUi() = ChatParticipantUi(
    id = userId,
    imageUrl = profilePictureUrl,
    name = userName,
    initials = initials
)

fun User.toUi() = ChatParticipantUi(
    id = id,
    name = userName,
    initials = userName.take(2).uppercase(),
    imageUrl = profilePicture
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
    val (local, other) = memberList.partition {
        println("check_local_list userId = ${it.userId}, localParticipantId = $localParticipantId")
        it.userId == localParticipantId
    }

    println("check_local_list Local List = ${local.joinToString("/n")}")

    return ChatUi(
        id = id,
        localParticipant = local.first().toUi(),
        remoteParticipants = other.map { it.toUi() },
        lastMessage = lastMessage,
        lastMessageSenderName = lastMessageSenderUsername
    )
}