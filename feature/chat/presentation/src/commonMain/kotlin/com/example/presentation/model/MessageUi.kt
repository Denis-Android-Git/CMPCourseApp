package com.example.presentation.model

import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.domain.models.DeliveryStatus
import com.example.presentation.util.UiText

sealed class MessageUi(open val id: String) {
    data class LocalUserMessage(
        override val id: String,
        val content: String,
        val deliveryStatus: DeliveryStatus,
        val formattedSentTime: UiText
    ) : MessageUi(id)

    data class OtherUserMessage(
        override val id: String,
        val content: String,
        val formattedSentTime: UiText,
        val sender: ChatParticipantUi
    ) : MessageUi(id)

    data class DateSeparator(
        override val id: String,
        val date: UiText
    ) : MessageUi(id)
}
