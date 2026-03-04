package com.example.data.message

import com.example.data.database.safeDbUpdate
import com.example.data.mappers.toDomain
import com.example.data.mappers.toEntity
import com.example.database.entities.MessageWithSender
import com.example.database.my_database.MyDataBase
import com.example.domain.message.ChatMessageService
import com.example.domain.message.MessageRepository
import com.example.domain.models.ChatMessage
import com.example.domain.models.DeliveryStatus
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult
import com.example.domain.util.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class OfflineFirstMessageRepository(
    private val myDataBase: MyDataBase,
    private val chatMessageService: ChatMessageService
) : MessageRepository {
    override suspend fun updateMessageDeliveryStatus(
        messageId: String,
        deliveryStatus: DeliveryStatus
    ): EmptyResult<DataError.Local> {
        return safeDbUpdate {
            myDataBase.chatMessageDao.updateDeliveryStatus(
                messageId,
                deliveryStatus.name,
                Clock.System.now().toEpochMilliseconds()
            )
        }
    }

    override suspend fun fetchMessages(
        chatId: String,
        before: String?
    ): CustomResult<List<ChatMessage>, DataError> {
        return chatMessageService
            .fetchMessages(chatId, before)
            .onSuccess { messages ->
                return safeDbUpdate {
                    val entities = messages.map { it.toEntity() }
                    myDataBase.chatMessageDao.upsertMessagesAndSyncIfNeeded(
                        chatId,
                        entities,
                        pageSize = ChatMessageConstants.PAGE_SIZE,
                        shouldDSync = before == null //Only sync for most recent page
                    )
                    messages
                }
            }

    }

    override fun getMessagesForChat(chatId: String): Flow<List<com.example.domain.models.MessageWithSender>> {
        return myDataBase.chatMessageDao.getMessagesByChatId(chatId)
            .map { messageEntities ->
                messageEntities.map { it.toDomain() }
            }
    }
}