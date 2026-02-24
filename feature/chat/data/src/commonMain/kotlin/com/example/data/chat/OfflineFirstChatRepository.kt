package com.example.data.chat

import com.example.data.lifeCycle.AppLifeCycleObserver
import com.example.data.logging.KermitLogger
import com.example.data.mappers.toDomain
import com.example.data.mappers.toEntity
import com.example.data.mappers.toLastMessageView
import com.example.data.network.ConnectivityObserver
import com.example.database.entities.ChatInfoEntity
import com.example.database.entities.ChatParticipantEntity
import com.example.database.entities.ChatWithParticipants
import com.example.database.my_database.MyDataBase
import com.example.domain.chat.ChatRepository
import com.example.domain.chat.ChatService
import com.example.domain.logging.MyLogger
import com.example.domain.models.Chat
import com.example.domain.models.ChatInfo
import com.example.domain.models.ChatParticipant
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult
import com.example.domain.util.asEmptyResult
import com.example.domain.util.onFailure
import com.example.domain.util.onSuccess
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.supervisorScope

@OptIn(DelicateCoroutinesApi::class)
class OfflineFirstChatRepository(
    private val chatService: ChatService,
    private val myDataBase: MyDataBase,
    private val myLogger: MyLogger = KermitLogger,
    connectivityObserver: ConnectivityObserver
) : ChatRepository {

    init {
        connectivityObserver.isConnected.onEach {
            myLogger.debug("App is in connected: $it")
        }.launchIn(GlobalScope)
    }

    override fun getChats(): Flow<List<Chat>> {
        return myDataBase.chatDao.getChatsWithParticipants()
            .map { participants ->
                supervisorScope {
                    participants.map { chatWithParticipants ->
                        async {
                            ChatWithParticipants(
                                chat = chatWithParticipants.chat,
                                participants = chatWithParticipants.participants.onlyActive(
                                    chatWithParticipants.chat.chatId
                                ),
                                lastMessageView = chatWithParticipants.lastMessageView
                            )
                        }
                    }.awaitAll()
                        .map { it.toDomain() }
                }
            }
    }

    override fun getActiveParticipantsByChatId(chatId: String): Flow<List<ChatParticipant>> {
        return myDataBase.chatDao.getActiveParticipantsByChatId(chatId)
            .map { participants ->
                participants.map {
                    it.toDomain()
                }
            }
    }

    override fun getChatInfoById(id: String): Flow<ChatInfo> {
        return myDataBase.chatDao.getChatInfoById(id)
            .filterNotNull()
            .map { chatInfoEntity ->
                ChatInfoEntity(
                    chat = chatInfoEntity.chat,
                    participants = chatInfoEntity.participants.onlyActive(chatInfoEntity.chat.chatId),
                    messagesWithSenders = chatInfoEntity.messagesWithSenders
                )
            }
            .map { it.toDomain() }
    }

    override suspend fun fetchChats(): CustomResult<List<Chat>, DataError.Remote> {
        return chatService.getChats()
            .onSuccess { serverChats ->
                val chats = serverChats.map { chat ->
                    ChatWithParticipants(
                        chat = chat.toEntity(),
                        participants = chat.memberList.map {
                            it.toEntity()
                        },
                        lastMessageView = chat.lastMessage?.toLastMessageView()
                    )
                }
                myDataBase.chatDao.upsertChatsWithParticipantsAndCrossRef(
                    chats = chats,
                    chatParticipantDao = myDataBase.chatParticipantDao,
                    chatParticipantCrossRefDao = myDataBase.chatParticipantCrossRefDao,
                    chatMessageDao = myDataBase.chatMessageDao
                )
            }
    }

    override suspend fun fetchChatById(id: String): EmptyResult<DataError.Remote> {
        return chatService.getChatById(id)
            .onSuccess { chat ->
                myDataBase.chatDao.upsertChatWithParticipantsAndCrossRef(
                    chat.toEntity(),
                    participants = chat.memberList.map { it.toEntity() },
                    chatParticipantDao = myDataBase.chatParticipantDao,
                    chatParticipantCrossRefDao = myDataBase.chatParticipantCrossRefDao
                )
            }.asEmptyResult()
    }

    override suspend fun createChat(otherUsersIds: List<String>): CustomResult<Chat, DataError.Remote> {
        return chatService.createChat(otherUsersIds)
            .onSuccess { chat ->
                myDataBase.chatDao.upsertChatWithParticipantsAndCrossRef(
                    chat.toEntity(),
                    participants = chat.memberList.map { it.toEntity() },
                    chatParticipantDao = myDataBase.chatParticipantDao,
                    chatParticipantCrossRefDao = myDataBase.chatParticipantCrossRefDao
                )
            }
    }

    override suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote> {
        return chatService
            .leaveChat(chatId)
            .onSuccess {
                myDataBase.chatDao.deleteChatById(chatId)
            }.asEmptyResult()
    }

    override suspend fun addPeopleToChat(
        chatId: String,
        userIds: List<String>
    ): CustomResult<Chat, DataError.Remote> {
        return chatService.addPeopleToChat(
            chatId = chatId,
            idsList = userIds
        )
            .onSuccess { chat ->
                myDataBase.chatDao.upsertChatWithParticipantsAndCrossRef(
                    chat = chat.toEntity(),
                    participants = chat.memberList.map { it.toEntity() },
                    chatParticipantDao = myDataBase.chatParticipantDao,
                    chatParticipantCrossRefDao = myDataBase.chatParticipantCrossRefDao
                )
            }
            .onFailure {

            }
    }

    private suspend fun List<ChatParticipantEntity>.onlyActive(chatId: String): List<ChatParticipantEntity> {
        val activeIds = myDataBase.chatDao.getActiveParticipantsByChatId(chatId)
            .first()
            .map { it.userId }
        return this.filter { it.userId in activeIds }

    }
}