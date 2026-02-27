package com.example.domain.message

import com.example.domain.models.DeliveryStatus
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult

interface MessageRepository {
    suspend fun updateMessageDeliveryStatus(
        messageId: String,
        deliveryStatus: DeliveryStatus
    ): EmptyResult<DataError.Local>
}