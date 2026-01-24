package com.example.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.database.entities.ChatParticipantEntity

@Dao
interface ChatParticipantDao {
    @Upsert
    suspend fun upsert(chatParticipant: ChatParticipantEntity)

    @Upsert
    suspend fun upsertAll(chatParticipants: List<ChatParticipantEntity>)

    @Query("SELECT * FROM chatparticipantentity ")
    suspend fun getAllChatParticipants(): List<ChatParticipantEntity>
}