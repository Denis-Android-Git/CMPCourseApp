package com.example.presentation.di

import com.example.presentation.chat_detail.ChatDetailViewModel
import com.example.presentation.chat_list.ChatListScreenViewModel
import com.example.presentation.chat_list_detail.ChatListDetailAdaptiveLayoutViewModel
import com.example.presentation.create_chat.CreateChatViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chatPresentationModule = module {
    viewModelOf(::ChatListScreenViewModel)
    viewModelOf(::ChatListDetailAdaptiveLayoutViewModel)
    viewModelOf(::CreateChatViewModel)
    viewModelOf(::ChatDetailViewModel)

}