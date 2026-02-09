package com.example.data.chat

import com.example.data.dto.ChatDto
import com.example.data.dto.request.CreateChatRequest
import com.example.data.mappers.toDomain
import com.example.data.network.get
import com.example.data.network.post
import com.example.domain.chat.ChatService
import com.example.domain.models.Chat
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import com.example.domain.util.map
import io.ktor.client.HttpClient

class KtorChatService(
    private val httpClient: HttpClient
) : ChatService {
    override suspend fun createChat(idList: List<String>): CustomResult<Chat, DataError.Remote> {
        return httpClient.post<CreateChatRequest, ChatDto>(
            route = "/chat",
            body = CreateChatRequest(
                otherUserIds = idList
            )
        ).map { it.toDomain() }
    }

    override suspend fun getChats(): CustomResult<List<Chat>, DataError.Remote> {
        return httpClient.get<List<ChatDto>>(
            route = "/chat"
        ).map { chatDtos ->
            chatDtos.map {
                it.toDomain()
            }
        }
    }

    override suspend fun getChatById(id: String): CustomResult<Chat, DataError.Remote> {
        return httpClient.get<ChatDto>(
            route = "/chat/$id"
        ).map {
            it.toDomain()
        }
    }
}