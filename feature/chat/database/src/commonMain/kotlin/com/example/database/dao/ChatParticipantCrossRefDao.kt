package com.example.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.database.entities.ChatParticipantCrossRef
import com.example.database.entities.ChatParticipantEntity

@Dao
interface ChatParticipantCrossRefDao {
    @Upsert
    suspend fun upsertCrossRefs(crossRefs: List<ChatParticipantCrossRef>)

    @Query("SELECT userId FROM chatparticipantcrossref WHERE chatId = :chatId AND isActive = 1")
    suspend fun getActiveParticipantIdsByChat(chatId: String): List<String>


    @Query("SELECT userId FROM chatparticipantcrossref WHERE chatId = :chatId")
    suspend fun getAllParticipantIdsByChat(chatId: String): List<String>

    @Query(
        """
        UPDATE chatparticipantcrossref
        SET isActive = 0
        WHERE chatId = :chatId AND userId IN (:userIds)
    """
    )
    suspend fun markParticipantsAsInactive(chatId: String, userIds: List<String>)

    @Query(
        """
        UPDATE chatparticipantcrossref
        SET isActive = 1
        WHERE chatId = :chatId AND userId IN (:userIds)
    """
    )
    suspend fun reactivateParticipants(chatId: String, userIds: List<String>)

    @Transaction
    suspend fun syncChatParticipants(chatId: String, participants: List<ChatParticipantEntity>) {
        if (participants.isEmpty()) {
            return
        }
        val serverParticipantsIds = participants.map {
            it.userId
        }.toSet()
        val localParticipantsIds = getAllParticipantIdsByChat(chatId).toSet()
        val activeLocalParticipantsIds = getActiveParticipantIdsByChat(chatId).toSet()
        val inActiveLocalParticipantsIds = localParticipantsIds - activeLocalParticipantsIds


        val participantsToReactivate = serverParticipantsIds.intersect(
            inActiveLocalParticipantsIds
        )
        val participantsToDeactivate = activeLocalParticipantsIds - serverParticipantsIds
        markParticipantsAsInactive(chatId, participantsToDeactivate.toList())
        reactivateParticipants(chatId, participantsToReactivate.toList())
        val completelyNewParticipantIds = serverParticipantsIds - localParticipantsIds
        val newCrossRefs = completelyNewParticipantIds.map {
            ChatParticipantCrossRef(
                chatId = chatId,
                userId = it,
                isActive = true
            )
        }
        upsertCrossRefs(newCrossRefs)
    }
}