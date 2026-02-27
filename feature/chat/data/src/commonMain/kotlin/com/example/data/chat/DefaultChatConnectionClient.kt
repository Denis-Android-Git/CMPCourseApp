package com.example.data.chat

import com.example.data.dto.ws.WsMessageDto
import com.example.data.mappers.toNewMessage
import com.example.data.network.KtorWebSocketConnector
import com.example.database.my_database.MyDataBase
import com.example.domain.auth.SessionStorage
import com.example.domain.chat.ChatConnectionClient
import com.example.domain.chat.ChatRepository
import com.example.domain.error.ConnectionError
import com.example.domain.message.MessageRepository
import com.example.domain.models.ChatMessage
import com.example.domain.models.DeliveryStatus
import com.example.domain.util.EmptyResult
import com.example.domain.util.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json

class WsChatConnectionClient(
    private val ktorWebSocketConnector: KtorWebSocketConnector,
    private val chatRepository: ChatRepository,
    private val dataBase: MyDataBase,
    private val sessionStorage: SessionStorage,
    private val json: Json,
    private val messageRepository: MessageRepository
) : ChatConnectionClient {
    override val chatMessages: Flow<ChatMessage>
        get() = TODO("Not yet implemented")
    override val connectionState = ktorWebSocketConnector.connectionState

    override suspend fun sendMessage(message: ChatMessage): EmptyResult<ConnectionError> {
        val outgoingDto = message.toNewMessage()
        val wsMessage = WsMessageDto(
            type = outgoingDto.type.name,
            payLoad = json.encodeToString(outgoingDto)
        )
        val rawJson = json.encodeToString(wsMessage)
        return ktorWebSocketConnector.sendMessage(rawJson)
            .onFailure {
                messageRepository.updateMessageDeliveryStatus(
                    messageId = message.id,
                    deliveryStatus = DeliveryStatus.FAILED
                )
            }
    }
}