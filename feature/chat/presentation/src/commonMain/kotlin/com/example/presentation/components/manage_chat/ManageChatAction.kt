package com.example.presentation.components.manage_chat

sealed interface ManageChatAction {
    data object OnAddClick : ManageChatAction
    data object OnDismissClick : ManageChatAction
    data object OnPrimaryActionClick : ManageChatAction

    sealed interface ChatPeople : ManageChatAction {
        data class OnSelectChat(val chatId: String?) : ManageChatAction
    }
}