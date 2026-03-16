package com.example.data.message

import com.example.data.database.safeDbUpdate
import com.example.data.dto.ws.OutgoingWsDto
import com.example.data.dto.ws.WsMessageDto
import com.example.data.mappers.toDomain
import com.example.data.mappers.toEntity
import com.example.data.mappers.toWsDto
import com.example.data.network.KtorWebSocketConnector
import com.example.database.my_database.MyDataBase
import com.example.domain.auth.SessionStorage
import com.example.domain.message.ChatMessageService
import com.example.domain.message.MessageRepository
import com.example.domain.models.ChatMessage
import com.example.domain.models.DeliveryStatus
import com.example.domain.models.MessageWithSender
import com.example.domain.models.OutgoingNewMessage
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult
import com.example.domain.util.onFailure
import com.example.domain.util.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Clock

class OfflineFirstMessageRepository(
    private val myDataBase: MyDataBase,
    private val chatMessageService: ChatMessageService,
    private val sessionStorage: SessionStorage,
    private val json: Json,
    private val webSocketConnector: KtorWebSocketConnector,
    private val applicationScope: CoroutineScope
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

    override suspend fun fetchMessages(
        chatId: String,
        before: String?
    ): CustomResult<List<ChatMessage>, DataError> {
        return chatMessageService
            .fetchMessages(chatId, before)
            .onSuccess { messages ->
                return safeDbUpdate {
                    val entities = messages.map { it.toEntity() }
                    myDataBase.chatMessageDao.upsertMessagesAndSyncIfNeeded(
                        chatId,
                        entities,
                        pageSize = ChatMessageConstants.PAGE_SIZE,
                        shouldDSync = before == null //Only sync for most recent page
                    )
                    messages
                }
            }

    }

    override fun getMessagesForChat(chatId: String): Flow<List<MessageWithSender>> {
        return myDataBase.chatMessageDao.getMessagesByChatId(chatId)
            .map { messageEntities ->
                messageEntities.map { it.toDomain() }
            }
    }

    override suspend fun sendMessage(message: OutgoingNewMessage): EmptyResult<DataError> {
        return safeDbUpdate {
            val dto = message.toWsDto()
            val localUser =
                sessionStorage.observeAuthInfo().first()?.user ?: return CustomResult.Failure(
                    DataError.Local.FILE_NOT_FOUND
                )
            val entity = dto.toEntity(localUser.id, deliveryStatus = DeliveryStatus.SENDING)
            myDataBase.chatMessageDao.upsertChatMessage(
                entity
            )
            return webSocketConnector
                .sendMessage(dto.toJsonPayload())
                .onFailure {
                    applicationScope.launch {
                        myDataBase.chatMessageDao.updateDeliveryStatus(
                            messageId = entity.messageId,
                            status = DeliveryStatus.FAILED.name,
                            timestamp = Clock.System.now().toEpochMilliseconds()
                        )
                    }.join()
                }
        }
    }

    override suspend fun retrySendingMessage(messageId: String): EmptyResult<DataError> {
        return safeDbUpdate {
            val message =
                myDataBase.chatMessageDao.getMessageById(messageId) ?: return CustomResult.Failure(
                    DataError.Local.FILE_NOT_FOUND
                )
            myDataBase.chatMessageDao.updateDeliveryStatus(
                messageId,
                DeliveryStatus.SENDING.name,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
            val outgoingNewMessage = OutgoingWsDto.NewMessage(
                messageId = messageId,
                chatId = message.chatId,
                content = message.content
            )
            return webSocketConnector.sendMessage(outgoingNewMessage.toJsonPayload())
                .onFailure {
                    applicationScope.launch {
                        myDataBase.chatMessageDao.updateDeliveryStatus(
                            messageId = messageId,
                            status = DeliveryStatus.FAILED.name,
                            timestamp = Clock.System.now().toEpochMilliseconds()
                        )
                    }.join()
                }
        }
    }

    private fun OutgoingWsDto.NewMessage.toJsonPayload(): String {
        val wsMessage = WsMessageDto(
            type = type.name,
            payload = json.encodeToString(this)
        )
        return json.encodeToString(wsMessage)
    }
}