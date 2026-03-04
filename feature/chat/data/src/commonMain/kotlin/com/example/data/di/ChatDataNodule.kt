package com.example.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.data.chat.KtorChatParticipantService
import com.example.data.chat.KtorChatService
import com.example.data.chat.OfflineFirstChatRepository
import com.example.data.chat.WsChatConnectionClient
import com.example.data.message.KtorChatMessageService
import com.example.data.message.OfflineFirstMessageRepository
import com.example.data.network.ConnectionRetryHandler
import com.example.data.network.KtorWebSocketConnector
import com.example.database.my_database.DbFactory
import com.example.domain.chat.ChatConnectionClient
import com.example.domain.chat.ChatParticipantService
import com.example.domain.chat.ChatRepository
import com.example.domain.chat.ChatService
import com.example.domain.message.ChatMessageService
import com.example.domain.message.MessageRepository
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


expect val platformChatDataModule: Module
val chatDataModule = module {
    singleOf(::KtorChatParticipantService) bind ChatParticipantService::class
    singleOf(::KtorChatService) bind ChatService::class
    singleOf(::OfflineFirstChatRepository) bind ChatRepository::class
    singleOf(::OfflineFirstMessageRepository) bind MessageRepository::class
    singleOf(::WsChatConnectionClient) bind ChatConnectionClient::class
    singleOf(::ConnectionRetryHandler)
    singleOf(::KtorWebSocketConnector)
    singleOf(::KtorChatMessageService) bind ChatMessageService::class
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
    includes(platformChatDataModule)
    single {
        get<DbFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}