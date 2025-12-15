package com.example.presentation.chat_list_detail

data class ChatListDetailAdaptiveLayoutState(
    val selectedChatId: String? = null,
    val dialogState: DialogState = DialogState.Hidden

)

sealed interface DialogState {
    data object Hidden : DialogState
    data object CreateChat : DialogState
    data class ManageChat(val chatId: String) : DialogState
    data object Profile : DialogState
}