package com.example.data.chat

import com.example.data.dto.ws.IncomingWsDto
import com.example.data.dto.ws.IncomingWsType
import com.example.data.dto.ws.WsMessageDto
import com.example.data.mappers.toDomain
import com.example.data.mappers.toEntity
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.serialization.json.Json

class WsChatConnectionClient(
    private val ktorWebSocketConnector: KtorWebSocketConnector,
    private val chatRepository: ChatRepository,
    private val dataBase: MyDataBase,
    private val sessionStorage: SessionStorage,
    private val json: Json,
    private val messageRepository: MessageRepository,
    applicationScope: CoroutineScope
) : ChatConnectionClient {
    override val chatMessages = ktorWebSocketConnector
        .messages
        .mapNotNull { parseIncomingMessage(it) }
        .onEach { handleIncomingMessage(it) }
        .filterIsInstance<IncomingWsDto.NewMessage>()
        .mapNotNull {
            dataBase.chatMessageDao.getMessageById(it.id)?.toDomain()
        }
        .shareIn(
            applicationScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
        )

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

    private fun parseIncomingMessage(message: WsMessageDto): IncomingWsDto? {
        return when (message.type) {
            IncomingWsType.NEW_MESSAGE.name -> {
                json.decodeFromString<IncomingWsDto.NewMessage>(message.payLoad)
            }

            IncomingWsType.MESSAGE_DELETED.name -> {
                json.decodeFromString<IncomingWsDto.MessageDeleted>(message.payLoad)
            }

            IncomingWsType.PROFILE_PICTURE_UPDATED.name -> {
                json.decodeFromString<IncomingWsDto.ProfilePictureUpdated>(message.payLoad)
            }

            IncomingWsType.CHAT_PARTICIPANTS_CHANGED.name -> {
                json.decodeFromString<IncomingWsDto.ChatParticipantsUpdated>(message.payLoad)
            }

            else -> null
        }
    }

    private suspend fun handleIncomingMessage(message: IncomingWsDto) {
        when (message) {
            is IncomingWsDto.ChatParticipantsUpdated -> refreshChat(message)
            is IncomingWsDto.MessageDeleted -> deleteMessage(message)
            is IncomingWsDto.NewMessage -> handleNewMessage(message)
            is IncomingWsDto.ProfilePictureUpdated -> updateProfilePicture(message)
        }
    }

    private suspend fun refreshChat(message: IncomingWsDto.ChatParticipantsUpdated) {
        chatRepository.fetchChatById(message.chatId)
    }

    private suspend fun deleteMessage(message: IncomingWsDto.MessageDeleted) {
        dataBase.chatMessageDao.getMessageById(message.messageId)
    }

    private suspend fun handleNewMessage(message: IncomingWsDto.NewMessage) {
        val chatExists = dataBase.chatDao.getChatById(message.chatId) != null
        if (!chatExists) {
            chatRepository.fetchChatById(message.chatId)
        }
        val entity = message.toEntity()
        dataBase.chatMessageDao.upsertChatMessage(entity)
    }

    private suspend fun updateProfilePicture(message: IncomingWsDto.ProfilePictureUpdated) {
        dataBase.chatParticipantDao.updateProfilePictureUrl(
            userId = message.userId,
            newUrl = message.newUrl
        )
        val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
        if (authInfo?.user?.id == message.userId) {
            sessionStorage.set(
                authInfo.copy(
                    user = authInfo.user.copy(
                        profilePicture = message.newUrl
                    )
                )
            )
        }
    }
}