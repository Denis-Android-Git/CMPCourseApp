package com.example.presentation.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.auth.SessionStorage
import com.example.domain.chat.ChatRepository
import com.example.presentation.mappers.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatListScreenViewModel(
    private val chatRepository: ChatRepository,
    sessionStorage: SessionStorage
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ChatListScreenState())
    val state = combine(
        _state,
        chatRepository.getChats(),
        sessionStorage.observeAuthInfo()
    ) { currentState, chats, authInfo ->
        if (authInfo == null) {
            return@combine ChatListScreenState()
        }
        currentState.copy(
            chats = chats.map { it.toUi(authInfo.user.id) },
            localParticipant = authInfo.user.toUi()
        )
    }.onStart {
        if (!hasLoadedInitialData) {
            fetchChats()
            hasLoadedInitialData = true
        }
    }.stateIn(
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

    private fun fetchChats() {
        viewModelScope.launch {
            chatRepository.fetchChats()
        }
    }
}