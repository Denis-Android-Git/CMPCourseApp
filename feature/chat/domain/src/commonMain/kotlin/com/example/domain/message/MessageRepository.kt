package com.example.domain.message

import com.example.domain.models.ChatMessage
import com.example.domain.models.DeliveryStatus
import com.example.domain.models.MessageWithSender
import com.example.domain.models.OutgoingNewMessage
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun updateMessageDeliveryStatus(
        messageId: String,
        deliveryStatus: DeliveryStatus
    ): EmptyResult<DataError.Local>

    suspend fun fetchMessages(
        chatId: String,
        before: String? = null
    ): CustomResult<List<ChatMessage>, DataError>

    fun getMessagesForChat(chatId: String): Flow<List<MessageWithSender>>
    suspend fun sendMessage(message: OutgoingNewMessage): EmptyResult<DataError>

    suspend fun retrySendingMessage(messageId: String): EmptyResult<DataError>
}