package com.example.presentation.chat_list

import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.presentation.model.ChatUi
import com.example.presentation.util.UiText

data class ChatListScreenState(
    val chats: List<ChatUi> = emptyList(),
    val error: UiText? = null,
    val localParticipant: ChatParticipantUi? = null,
    val isUserMenuOpen: Boolean = false,
    val showLogoutConfirmationDialog: Boolean = false,
    val selectedChatId: String? = null,
    val isLoading: Boolean = false
)