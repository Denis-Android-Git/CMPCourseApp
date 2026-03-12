package com.example.presentation.chat_list_detail

sealed interface ChatListDetailAdaptiveLayoutAction {
    data class OnSelectChat(val chatId: String?) : ChatListDetailAdaptiveLayoutAction
    data object OnProfileSettingsClicked : ChatListDetailAdaptiveLayoutAction
    data object OnCreateChatClicked : ChatListDetailAdaptiveLayoutAction
    data object OnManageChatClicked : ChatListDetailAdaptiveLayoutAction
    data object OnDismissCurrentChatClicked : ChatListDetailAdaptiveLayoutAction
}