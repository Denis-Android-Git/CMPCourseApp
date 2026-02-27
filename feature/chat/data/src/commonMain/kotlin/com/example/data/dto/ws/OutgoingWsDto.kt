package com.example.data.dto.ws

import kotlinx.serialization.Serializable

enum class OutgoingWsType {
    NEW_MESSAGE

}

@Serializable
sealed class OutgoingWsDto(
    val type: OutgoingWsType
) {
    @Serializable
    data class NewMessage(
        val messageId: String,
        val chatId: String,
        val content: String
    ) : OutgoingWsDto(OutgoingWsType.NEW_MESSAGE)
}
