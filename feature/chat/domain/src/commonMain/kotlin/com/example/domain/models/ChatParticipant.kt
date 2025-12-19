package com.example.domain.models

data class ChatParticipant(
    val userId: String,
    val userName: String,
    val profilePictureUrl: String?
) {
    val initials: String
        get() = userName.take(2).uppercase()
}
