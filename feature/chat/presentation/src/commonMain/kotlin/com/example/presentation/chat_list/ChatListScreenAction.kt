package com.example.presentation.chat_list

import com.example.presentation.model.ChatUi

sealed interface ChatListScreenAction {
    data object OnUsrAvatarClicked : ChatListScreenAction
    data object OnProfileSettingsClicked : ChatListScreenAction
    data class OnChatClicked(val chat: ChatUi) : ChatListScreenAction
    data object OnDismissUserMenu : ChatListScreenAction
    data object OnLogoutClicked : ChatListScreenAction
    data object OnConfirmLogoutClicked : ChatListScreenAction
    data object OnDismissLogoutDialog : ChatListScreenAction
    data object OnCreateChatClicked : ChatListScreenAction
}