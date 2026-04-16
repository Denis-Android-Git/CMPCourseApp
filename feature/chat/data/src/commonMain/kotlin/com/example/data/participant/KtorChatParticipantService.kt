package com.example.data.participant

import com.example.data.dto.ChatParticipantDto
import com.example.data.dto.request.ConfirmProfilePictureRequest
import com.example.data.dto.response.ProfilePictureUploadUrlsResponse
import com.example.data.mappers.toDomain
import com.example.data.network.constructRoute
import com.example.data.network.get
import com.example.data.network.post
import com.example.data.network.put
import com.example.data.network.safeCall
import com.example.domain.participant.ChatParticipantService
import com.example.domain.models.ChatParticipant
import com.example.domain.models.ProfilePictureUploadUrls
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult
import com.example.domain.util.map
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import kotlin.collections.component1
import kotlin.collections.component2

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

    override suspend fun getLocalUser(): CustomResult<ChatParticipant, DataError.Remote> {
        return httpClient.get<ChatParticipantDto>(
            route = "/participants"
        ).map {
            it.toDomain()
        }
    }

    override suspend fun uploadProfilePicture(
        uploadUrl: String,
        bytes: ByteArray,
        headers: Map<String, String>
    ): EmptyResult<DataError.Remote> {
        return safeCall {
            httpClient.put {
                url(uploadUrl)
                headers.forEach { (key, value) ->
                    header(key, value)
                }
                setBody(bytes)
            }
        }
    }

    override suspend fun getPictureUploadUrl(mimeTye: String): CustomResult<ProfilePictureUploadUrls, DataError.Remote> {
        return httpClient.post<Unit, ProfilePictureUploadUrlsResponse>(
            route = "/participants/profile-picture-upload",
            queryParams = mapOf(
                "mimeType" to mimeTye
            ),
            body = Unit
        ).map {
            it.toDomain()
        }
    }

    override suspend fun confirmPictureUpload(publicUrl: String): EmptyResult<DataError.Remote> {
        return httpClient.post<ConfirmProfilePictureRequest, Unit>(
            route = "/participants/confirm-profile-picture",
            body = ConfirmProfilePictureRequest(publicUrl)
        )
    }
}