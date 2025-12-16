package com.example.presentation.create_chat

sealed interface CreateChatAction {
    data object OnAddClick : CreateChatAction
    data object OnDismissClick : CreateChatAction
    data object OnCreateChatClick : CreateChatAction
}