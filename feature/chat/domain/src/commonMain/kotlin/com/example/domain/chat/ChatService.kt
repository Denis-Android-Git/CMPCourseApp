package com.example.domain.chat

import com.example.domain.models.Chat
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError

interface ChatService {
    suspend fun createChat(
        idList: List<String>
    ): CustomResult<Chat, DataError.Remote>
}