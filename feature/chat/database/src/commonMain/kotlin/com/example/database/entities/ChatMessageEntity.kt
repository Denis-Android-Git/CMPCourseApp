package com.example.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = ["chatId"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val chatId: String,
    val senderId: String,
    val content: String,
    val timeStamp: Long,
    val deliveryStatus: String,
    val deliveryStatusTimeStamp: Long = timeStamp
    )
