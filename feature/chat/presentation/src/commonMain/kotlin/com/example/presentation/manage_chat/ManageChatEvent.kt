package com.example.presentation.manage_chat

sealed interface ManageChatEvent {
    data object OnMembersAdded : ManageChatEvent
}