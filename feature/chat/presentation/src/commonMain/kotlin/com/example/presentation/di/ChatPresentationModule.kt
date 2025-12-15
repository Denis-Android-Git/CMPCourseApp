package com.example.presentation.di

import com.example.presentation.chat_list.ChatListScreenViewModel
import com.example.presentation.chat_list.TestViewmodel
import com.example.presentation.chat_list_detail.ChatListDetailAdaptiveLayoutViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chatPresentationModule = module {
    viewModelOf(::ChatListScreenViewModel)
    viewModelOf(::TestViewmodel)
    viewModelOf(::ChatListDetailAdaptiveLayoutViewModel)

}