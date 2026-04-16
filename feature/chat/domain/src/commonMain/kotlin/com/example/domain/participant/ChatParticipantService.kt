package com.example.domain.participant

import com.example.domain.models.ChatParticipant
import com.example.domain.models.ProfilePictureUploadUrls
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult

interface ChatParticipantService {
    suspend fun search(query: String): CustomResult<ChatParticipant, DataError.Remote>

    suspend fun getLocalUser(): CustomResult<ChatParticipant, DataError.Remote>

    suspend fun uploadProfilePicture(
        uploadUrl: String,
        bytes: ByteArray,
        headers: Map<String, String>
    ): EmptyResult<DataError.Remote>

    suspend fun getPictureUploadUrl(
        mimeTye: String
    ): CustomResult<ProfilePictureUploadUrls, DataError.Remote>

    suspend fun confirmPictureUpload(
        publicUrl: String
    ): EmptyResult<DataError.Remote>
}