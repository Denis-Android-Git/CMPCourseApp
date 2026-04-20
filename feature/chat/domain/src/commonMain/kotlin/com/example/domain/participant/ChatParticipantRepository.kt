package com.example.domain.participant

import com.example.domain.models.ChatParticipant
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult

interface ChatParticipantRepository {
    suspend fun fetchLocalUser(): CustomResult<ChatParticipant, DataError>
    suspend fun uploadProfilePicture(
        bytes: ByteArray,
        mimeType: String
    ): EmptyResult<DataError.Remote>

    suspend fun deleteProfilePicture(): EmptyResult<DataError.Remote>
}