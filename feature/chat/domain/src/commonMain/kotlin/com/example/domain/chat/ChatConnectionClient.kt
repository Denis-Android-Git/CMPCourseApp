package com.example.domain.chat

import com.example.domain.error.ConnectionError
import com.example.domain.models.ChatMessage
import com.example.domain.models.ConnectionState
import com.example.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ChatConnectionClient {
    val chatMessages: Flow<ChatMessage>
    val connectionState: StateFlow<ConnectionState>
    suspend fun sendMessage(message: ChatMessage): EmptyResult<ConnectionError>
}