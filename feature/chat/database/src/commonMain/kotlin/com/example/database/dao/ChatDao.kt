package com.example.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.database.entities.ChatEntity
import com.example.database.entities.ChatInfoEntity
import com.example.database.entities.ChatParticipantCrossRef
import com.example.database.entities.ChatParticipantEntity
import com.example.database.entities.ChatWithParticipants
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Upsert
    suspend fun upsertChat(chat: ChatEntity)

    @Upsert
    suspend fun upsertChats(chats: List<ChatEntity>)

    @Query("DELETE FROM chatentity WHERE chatId = :chatId")
    suspend fun deleteChatById(chatId: String)

    @Query("SELECT * FROM chatentity ORDER BY lastActivityAt DESC")
    @Transaction
    fun getChatsWithParticipants(): Flow<List<ChatWithParticipants>>

    @Query("SELECT * FROM chatentity WHERE chatId = :id")
    @Transaction
    suspend fun getChatById(id: String): ChatWithParticipants?

    @Query("DELETE FROM chatentity")
    suspend fun deleteAllChats()

    @Query("SELECT chatId FROM chatentity")
    suspend fun getAllChatIds(): List<String>

    @Transaction
    suspend fun deleteChatsByIds(chatIds: List<String>) {
        chatIds.forEach { chatId ->
            deleteChatById(chatId)
        }
    }

    @Query("SELECT COUNT(*) FROM chatentity")
    fun getChatCount(): Flow<Int>

    @Query(
        """
        SELECT p.*
        FROM chatparticipantentity p
        JOIN chatparticipantcrossref cpcr ON p.userId = cpcr.userId
        WHERE cpcr.chatId = :chatId AND cpcr.isActive = true
        ORDER BY p.username
    """
    )
    fun getActiveParticipantsByChatId(chatId: String): Flow<List<ChatParticipantEntity>>

    @Query("SELECT * FROM chatentity WHERE chatId = :chatId")
    @Transaction
    fun getChatInfoById(chatId: String): Flow<ChatInfoEntity?>

    @Transaction
    suspend fun upsertChatWithParticipantsAndCrossRef(
        chat: ChatEntity,
        participants: List<ChatParticipantEntity>,
        chatParticipantDao: ChatParticipantDao,
        chatParticipantCrossRefDao: ChatParticipantCrossRefDao
    ) {
        upsertChat(chat)
        chatParticipantDao.upsertAll(participants)
        val crossRefs = participants.map {
            ChatParticipantCrossRef(
                chatId = chat.chatId,
                userId = it.userId,
                isActive = true
            )
        }
        chatParticipantCrossRefDao.upsertCrossRefs(crossRefs)
        chatParticipantCrossRefDao.syncChatParticipants(chat.chatId, participants)
    }


    @Transaction
    suspend fun upsertChatsWithParticipantsAndCrossRef(
        chats: List<ChatWithParticipants>,
        crossRefs: List<ChatParticipantCrossRef>,
        chatParticipantDao: ChatParticipantDao,
        chatParticipantCrossRefDao: ChatParticipantCrossRefDao
    ) {
        upsertChats(chats.map { it.chat })
        val allParticipants = chats.flatMap { it.participants }
        chatParticipantDao.upsertAll(allParticipants)
        val allCrossRefs = chats.flatMap { chatWithParticipants ->
            chatWithParticipants.participants.map {
                ChatParticipantCrossRef(
                    chatId = chatWithParticipants.chat.chatId,
                    userId = it.userId,
                    isActive = true
                )
            }
        }
        chatParticipantCrossRefDao.upsertCrossRefs(allCrossRefs)
        chats.forEach {
            chatParticipantCrossRefDao.syncChatParticipants(
                chatId = it.chat.chatId,
                participants = it.participants
            )
        }
    }
}