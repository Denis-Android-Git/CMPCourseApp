package com.example.domain.models

data class MessageWithSender(
    val message: ChatMessage,
    val sender: ChatMember,
    val deliveryStatus: DeliveryStatus?
)
