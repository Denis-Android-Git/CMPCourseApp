package com.example.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class PeopleRequest(
    val userIds: List<String>
)
