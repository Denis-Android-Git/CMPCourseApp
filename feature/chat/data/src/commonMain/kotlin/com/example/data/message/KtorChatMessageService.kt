package com.example.data.message

import com.example.data.dto.ChatMessageDto
import com.example.data.mappers.toDomain
import com.example.data.network.delete
import com.example.data.network.get
import com.example.domain.message.ChatMessageService
import com.example.domain.models.ChatMessage
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult
import com.example.domain.util.map
import io.ktor.client.HttpClient

class KtorChatMessageService(
    private val httpClient: HttpClient
) : ChatMessageService {
    override suspend fun fetchMessages(
        chatId: String,
        before: String?
    ): CustomResult<List<ChatMessage>, DataError.Remote> {
        return httpClient.get<List<ChatMessageDto>>(
            route = "/chat/$chatId/messages",
            queryParams = buildMap {
                this["pageSize"] = ChatMessageConstants.PAGE_SIZE
                if (before != null) {
                    this["before"] = before
                }
            }
        ).map { messages -> messages.map { it.toDomain() } }
    }

    override suspend fun deleteMessage(messageId: String): EmptyResult<DataError.Remote> {
        return httpClient.delete(
            route = "/messages/$messageId"
        )
    }
}