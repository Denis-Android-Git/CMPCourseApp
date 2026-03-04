package com.example.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.database.entities.ChatMessageEntity
import com.example.database.entities.MessageWithSender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface ChatMessageDao {
    @Upsert
    suspend fun upsertMessages(messages: List<ChatMessageEntity>)

    @Upsert
    suspend fun upsertChatMessage(chatMessage: ChatMessageEntity)

    @Query("DELETE FROM chatmessageentity WHERE messageId =  :id")
    suspend fun deleteChatMessage(id: Int)

    @Query("DELETE FROM chatmessageentity WHERE messageId IN (:messageIds)")
    suspend fun deleteAllMessages(messageIds: List<String>)

    @Query("SELECT * FROM chatmessageentity WHERE chatId = :chatId ORDER BY timestamp DESC")
    fun getMessagesByChatId(chatId: String): Flow<List<MessageWithSender>>

    @Query(
        """
        SELECT * FROM chatmessageentity 
        WHERE chatId = :chatId 
        ORDER BY timestamp DESC 
        LIMIT :limit
    """
    )
    fun getMessagesByChatIdLimited(chatId: String, limit: Int): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chatmessageentity WHERE messageId = :messageId")
    suspend fun getMessageById(messageId: String): ChatMessageEntity?

    @Query(
        """
        UPDATE chatmessageentity 
        SET deliveryStatus = :status, timestamp = :timestamp
        WHERE messageId = :messageId
    """
    )
    suspend fun updateDeliveryStatus(messageId: String, status: String, timestamp: Long)

    @Transaction
    suspend fun upsertMessagesAndSyncIfNeeded(
        chatId: String,
        messages: List<ChatMessageEntity>,
        pageSize: Int,
        shouldDSync: Boolean = false
    ) {
        val localMessages = getMessagesByChatIdLimited(chatId, pageSize).first()
        upsertMessages(messages)
        if (!shouldDSync) {
            return
        }
        val serverIds = messages.map { it.messageId }.toSet()
        val messagesToDelete = localMessages.filter {
            val missingOnServer = it.messageId !in serverIds
            val isSent = it.deliveryStatus == "SENT"
            missingOnServer && isSent
        }
        val messageIds = messagesToDelete.map { it.messageId }
        deleteAllMessages(messageIds)

    }
}