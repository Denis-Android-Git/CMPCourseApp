package com.example.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity
data class ChatParticipantEntity(
    @PrimaryKey
    val userId: String,
    val userName: String,
    val profilePictureUrl: String?,
)
