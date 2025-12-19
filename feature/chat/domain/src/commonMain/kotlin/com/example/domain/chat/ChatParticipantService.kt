package com.example.domain.chat

import com.example.domain.models.ChatParticipant
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError

interface ChatParticipantService {
    suspend fun search(query: String): CustomResult<ChatParticipant, DataError.Remote>
}