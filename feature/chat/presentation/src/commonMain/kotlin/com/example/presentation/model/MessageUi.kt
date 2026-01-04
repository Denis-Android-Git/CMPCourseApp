package com.example.presentation.model

import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.domain.models.DeliveryStatus
import com.example.presentation.util.UiText

sealed interface MessageUi {
    data class LocalUserMessage(
        val id: String,
        val content: String,
        val deliveryStatus: DeliveryStatus,
        val formattedSentTime: UiText,
        val isMenuOpen: Boolean
    ) : MessageUi

    data class OtherUserMessage(
        val id: String,
        val content: String,
        val formattedSentTime: UiText,
        val sender: ChatParticipantUi
    ) : MessageUi

    data class DateSeparator(
        val id: String,
        val date: UiText,

        ) : MessageUi
}
