package com.example.domain.models

import kotlin.time.Instant

data class Chat(
    val id: String,
    val memberList: List<ChatMember>,
    val lastActivityAt: Instant,
    val lastMessage: String?
)
