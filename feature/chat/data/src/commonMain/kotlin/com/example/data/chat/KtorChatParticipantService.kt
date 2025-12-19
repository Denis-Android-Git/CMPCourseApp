package com.example.data.chat

import com.example.data.dto.ChatParticipantDto
import com.example.data.mappers.toDomain
import com.example.data.network.get
import com.example.domain.chat.ChatParticipantService
import com.example.domain.models.ChatParticipant
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import com.example.domain.util.map
import io.ktor.client.HttpClient

class KtorChatParticipantService(
    private val httpClient: HttpClient
) : ChatParticipantService {
    override suspend fun search(query: String): CustomResult<ChatParticipant, DataError.Remote> {
        return httpClient.get<ChatParticipantDto>(
            route = "/participants",
            queryParams = mapOf("query" to query)
        ).map {
            it.toDomain()
        }
    }
}