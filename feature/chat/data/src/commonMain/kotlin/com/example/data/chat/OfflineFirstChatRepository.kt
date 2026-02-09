package com.example.data.chat

import com.example.data.mappers.toDomain
import com.example.data.mappers.toEntity
import com.example.data.mappers.toLastMessageView
import com.example.database.entities.ChatWithParticipants
import com.example.database.my_database.MyDataBase
import com.example.domain.chat.ChatRepository
import com.example.domain.chat.ChatService
import com.example.domain.models.Chat
import com.example.domain.models.ChatInfo
import com.example.domain.util.CustomResult
import com.example.domain.util.DataError
import com.example.domain.util.EmptyResult
import com.example.domain.util.asEmptyResult
import com.example.domain.util.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class OfflineFirstChatRepository(
    private val chatService: ChatService,
    private val myDataBase: MyDataBase
) : ChatRepository {
    override fun getChats(): Flow<List<Chat>> {
        return myDataBase.chatDao.getChatsWithActiveParticipants()
            .map { participants ->
                participants.map {
                    it.toDomain()
                }
            }
    }

    override fun getChatInfoById(id: String): Flow<ChatInfo> {
        return myDataBase.chatDao.getChatInfoById(id)
            .filterNotNull()
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

}