package com.example.data.dto.ws

import kotlinx.serialization.Serializable

enum class IncomingWsType {
    NEW_MESSAGE,
    MESSAGE_DELETED,
    PROFILE_PICTURE_UPDATED,
    CHAT_PARTICIPANTS_CHANGED

}


@Serializable
sealed class IncomingWsDto(
    val type: IncomingWsType
) {
    @Serializable
    data class NewMessage(
        val id: String,
        val chatId: String,
        val senderId: String,
        val content: String,
        val createdAt: String
    ) : IncomingWsDto(IncomingWsType.NEW_MESSAGE)

    @Serializable
    data class MessageDeleted(
        val messageId: String,
        val chatId: String
    ) : IncomingWsDto(IncomingWsType.MESSAGE_DELETED)

    @Serializable
    data class ProfilePictureUpdated(
        val userId: String,
        val newUrl: String?
    ) : IncomingWsDto(IncomingWsType.PROFILE_PICTURE_UPDATED)

    @Serializable
    data class ChatParticipantsUpdated(
        val chatId: String
    ) : IncomingWsDto(IncomingWsType.CHAT_PARTICIPANTS_CHANGED)
}