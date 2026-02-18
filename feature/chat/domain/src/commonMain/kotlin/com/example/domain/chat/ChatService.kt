package com.example.domain.chat

import com.example.domain.models.Chat
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult

interface ChatService {
    suspend fun createChat(
        idList: List<String>
    ): CustomResult<Chat, DataError.Remote>

    suspend fun getChats(): CustomResult<List<Chat>, DataError.Remote>
    suspend fun getChatById(id: String): CustomResult<Chat, DataError.Remote>

    suspend fun leaveChat(id: String): EmptyResult<DataError.Remote>

    suspend fun addPeopleToChat(
        chatId: String,
        idsList: List<String>
    ): CustomResult<Chat, DataError.Remote>

}