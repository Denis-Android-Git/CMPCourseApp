package com.example.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ProfilePictureUploadUrlsResponse(
    val uploadUrl: String,
    val publicUrl: String,
    val headers: Map<String, String>
)
