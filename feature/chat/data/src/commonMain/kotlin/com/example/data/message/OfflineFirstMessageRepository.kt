package com.example.data.message

import com.example.data.database.safeDbUpdate
import com.example.database.my_database.MyDataBase
import com.example.domain.message.MessageRepository
import com.example.domain.models.DeliveryStatus
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult
import kotlin.time.Clock

class OfflineFirstMessageRepository(
    private val myDataBase: MyDataBase
) : MessageRepository {
    override suspend fun updateMessageDeliveryStatus(
        messageId: String,
        deliveryStatus: DeliveryStatus
    ): EmptyResult<DataError.Local> {
        return safeDbUpdate {
            myDataBase.chatMessageDao.updateDeliveryStatus(
                messageId,
                deliveryStatus.name,
                Clock.System.now().toEpochMilliseconds()
            )
        }
    }
}