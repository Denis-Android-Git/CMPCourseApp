package com.example.domain.chat

import com.example.domain.models.Chat
import com.example.domain.models.ChatInfo
import com.example.domain.models.ChatParticipant
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<List<Chat>>
    fun getActiveParticipantsByChatId(chatId: String): Flow<List<ChatParticipant>>
    fun getChatInfoById(id: String): Flow<ChatInfo>
    suspend fun fetchChats(): CustomResult<List<Chat>, DataError.Remote>
    suspend fun fetchChatById(id: String): EmptyResult<DataError.Remote>

    suspend fun createChat(otherUsersIds: List<String>): CustomResult<Chat, DataError.Remote>
    suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote>
    suspend fun addPeopleToChat(
        chatId: String,
        userIds: List<String>
    ): CustomResult<Chat, DataError.Remote>
}