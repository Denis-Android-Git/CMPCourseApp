package com.example.data.dto.ws

import kotlinx.serialization.Serializable

enum class IncomingWsType {
    NEW_MESSAGE,
    MESSAGE_DELETED,
    PROFILE_PICTURE_UPDATED,
    CHAT_PARTICIPANTS_CHANGED

}


@Serializable
sealed interface IncomingWsDto {
    @Serializable
    data class NewMessage(
        val id: String,
        val chatId: String,
        val senderId: String,
        val content: String,
        val createdAt: String,
        val type: IncomingWsType = IncomingWsType.NEW_MESSAGE
    ) : IncomingWsDto

    @Serializable
    data class MessageDeleted(
        val messageId: String,
        val chatId: String,
        val type: IncomingWsType = IncomingWsType.MESSAGE_DELETED
    ) : IncomingWsDto

    @Serializable
    data class ProfilePictureUpdated(
        val userId: String,
        val newUrl: String?,
        val type: IncomingWsType = IncomingWsType.PROFILE_PICTURE_UPDATED
    ) : IncomingWsDto

    @Serializable
    data class ChatParticipantsUpdated(
        val chatId: String,
        val type: IncomingWsType = IncomingWsType.CHAT_PARTICIPANTS_CHANGED
    ) : IncomingWsDto
}