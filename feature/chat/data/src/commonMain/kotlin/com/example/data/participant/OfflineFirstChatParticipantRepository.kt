package com.example.data.participant

import com.example.domain.auth.SessionStorage
import com.example.domain.models.ChatParticipant
import com.example.domain.participant.ChatParticipantRepository
import com.example.domain.participant.ChatParticipantService
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult
import com.example.domain.util.onSuccess
import kotlinx.coroutines.flow.first

class OfflineFirstChatParticipantRepository(
    private val sessionStorage: SessionStorage,
    private val chatParticipantService: ChatParticipantService
) : ChatParticipantRepository {
    override suspend fun fetchLocalUser(): CustomResult<ChatParticipant, DataError> {
        return chatParticipantService
            .getLocalUser()
            .onSuccess {
                val currentInfo = sessionStorage.observeAuthInfo().first()
                sessionStorage.set(
                    currentInfo?.copy(
                        user = currentInfo.user.copy(
                            id = it.userId,
                            userName = it.userName,
                            profilePicture = it.profilePictureUrl
                        )
                    )
                )
            }
    }

    override suspend fun uploadProfilePicture(
        bytes: ByteArray,
        mimeType: String
    ): EmptyResult<DataError.Remote> {
        val result = chatParticipantService
            .getPictureUploadUrl(mimeType)

        if (result is CustomResult.Failure) {
            return result
        }
        val uploadUrls = (result as CustomResult.Success).data

        val uploadResult = chatParticipantService.uploadProfilePicture(
            uploadUrl = uploadUrls.uploadUrl,
            bytes = bytes,
            headers = uploadUrls.headers
        )
        if (uploadResult is CustomResult.Failure) {
            return uploadResult
        }
        return chatParticipantService
            .confirmPictureUpload(uploadUrls.publicUrl)
            .onSuccess {
                val currentInfo = sessionStorage.observeAuthInfo().first()
                sessionStorage.set(
                    currentInfo?.copy(
                        user = currentInfo.user.copy(
                            profilePicture = uploadUrls.publicUrl
                        )
                    )
                )
            }
    }

    override suspend fun deleteProfilePicture(): EmptyResult<DataError.Remote> {
        return chatParticipantService.deleteProfilePicture()
            .onSuccess {
                val authInfo = sessionStorage.observeAuthInfo().first()
                sessionStorage.set(
                    authInfo?.copy(
                        user = authInfo.user.copy(
                            profilePicture = null
                        )
                    )
                )
            }
    }
}