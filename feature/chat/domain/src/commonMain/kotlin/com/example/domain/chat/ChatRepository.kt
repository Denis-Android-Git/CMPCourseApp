package com.example.domain.chat

import com.example.domain.models.Chat
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<List<Chat>>
    suspend fun fetchChats(): CustomResult<List<Chat>, DataError.Remote>
}