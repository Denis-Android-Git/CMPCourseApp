package com.example.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.database.entities.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Upsert
    fun upsertMessages(messages: List<ChatMessageEntity>)

    @Upsert
    fun upsertChatMessage(chatMessage: ChatMessageEntity)

    @Query("DELETE FROM chatmessageentity WHERE id =  :id")
    fun deleteChatMessage(id: Int)

    @Query("DELETE FROM chatmessageentity WHERE id IN (:messageIds)")
    fun deleteAllMessages(messageIds: List<String>)

    @Query("SELECT * FROM chatmessageentity WHERE chatId = :chatId ORDER BY timestamp DESC")
    fun getMessagesByChatId(chatId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chatmessageentity WHERE id = :messageId")
    fun getMessageById(messageId: String): ChatMessageEntity?
}