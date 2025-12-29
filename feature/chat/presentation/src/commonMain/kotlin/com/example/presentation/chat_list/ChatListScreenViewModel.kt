package com.example.presentation.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class ChatListScreenViewModel(
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ChatListScreenState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                /** Load initial data here **/
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ChatListScreenState()
        )

    fun onAction(action: ChatListScreenAction) {
        when (action) {
            is ChatListScreenAction.OnChatClicked -> {}
            ChatListScreenAction.OnConfirmLogoutClicked -> {}
            ChatListScreenAction.OnDismissLogoutDialog -> {}
            ChatListScreenAction.OnDismissUserMenu -> {}
            ChatListScreenAction.OnLogoutClicked -> {}
            ChatListScreenAction.OnUsrAvatarClicked -> {}
            ChatListScreenAction.OnCreateChatClicked -> {}
            ChatListScreenAction.OnProfileSettingsClicked -> {}
        }
    }
}