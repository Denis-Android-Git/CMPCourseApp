package com.example.presentation.chat_list

sealed interface ChatListScreenAction {
    data object Decrypt : ChatListScreenAction
    data object Decrypt2 : ChatListScreenAction

    data object Encrypt : ChatListScreenAction


}