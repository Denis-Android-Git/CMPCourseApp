package com.example.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.data.chat.KtorChatParticipantService
import com.example.data.chat.KtorChatService
import com.example.database.my_database.DbFactory
import com.example.domain.chat.ChatParticipantService
import com.example.domain.chat.ChatService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


expect val platformChatDataModule: Module
val chatDataModule = module {
    singleOf(::KtorChatParticipantService) bind ChatParticipantService::class
    singleOf(::KtorChatService) bind ChatService::class
    includes(platformChatDataModule)
    single {
        get<DbFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}