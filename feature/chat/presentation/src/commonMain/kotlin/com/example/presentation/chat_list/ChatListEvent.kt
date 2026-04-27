package com.example.presentation.chat_list

import com.example.presentation.util.UiText

sealed interface ChatListEvent {
    data object OnLogOutSuccess : ChatListEvent
    data class OnLogOutError(val error: UiText) : ChatListEvent
}