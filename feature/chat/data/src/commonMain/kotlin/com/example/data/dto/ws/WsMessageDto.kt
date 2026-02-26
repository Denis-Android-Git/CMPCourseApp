package com.example.data.dto.ws

import kotlinx.serialization.Serializable

@Serializable
data class WsMessageDto(
    val type: String,
    val payLoad: String
)
