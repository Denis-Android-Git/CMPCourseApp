package com.example.data.di

import com.example.data.chat.KtorChatParticipantService
import com.example.data.chat.KtorChatService
import com.example.domain.chat.ChatParticipantService
import com.example.domain.chat.ChatService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val chatDataModule = module {
    singleOf(::KtorChatParticipantService) bind ChatParticipantService::class
    singleOf(::KtorChatService) bind ChatService::class

}