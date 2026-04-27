package com.example.presentation.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.auth.AuthService
import com.example.domain.auth.SessionStorage
import com.example.domain.chat.ChatRepository
import com.example.domain.notification.DeviceTokenService
import com.example.domain.util.onFailure
import com.example.domain.util.onSuccess
import com.example.presentation.mappers.toUi
import com.example.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatListScreenViewModel(
    private val chatRepository: ChatRepository,
    private val sessionStorage: SessionStorage,
    private val deviceTokenService: DeviceTokenService,
    private val authService: AuthService
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val eventChannel = Channel<ChatListEvent>()

    val events = eventChannel.receiveAsFlow()

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
            chats = chats.map {
                it.toUi(authInfo.user.id)
            },
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
            is ChatListScreenAction.OnSelectChat -> {
                _state.update {
                    it.copy(
                        selectedChatId = action.chatId
                    )
                }
            }

            ChatListScreenAction.OnConfirmLogoutClicked -> logOut()
            ChatListScreenAction.OnDismissLogoutDialog -> {
                _state.update { it.copy(showLogoutConfirmationDialog = false) }
            }

            ChatListScreenAction.OnLogoutClicked -> showLogOutConfirmation()
            ChatListScreenAction.OnProfileSettingsClicked,
            ChatListScreenAction.OnDismissUserMenu -> {
                _state.update { it.copy(isUserMenuOpen = false) }

            }

            ChatListScreenAction.OnUsrAvatarClicked -> {
                _state.update { it.copy(isUserMenuOpen = true) }
            }

            ChatListScreenAction.OnCreateChatClicked -> {}
            ChatListScreenAction.OnProfileSettingsClicked -> {}
        }
    }

    private fun logOut() {
        _state.update { it.copy(showLogoutConfirmationDialog = false) }
        viewModelScope.launch {
            val authInfo = sessionStorage.observeAuthInfo().first()
            val refreshToken = authInfo?.refreshToken ?: return@launch
            deviceTokenService.unregisterToken(refreshToken)
                .onSuccess {
                    authService.logOut(refreshToken)
                        .onSuccess {
                            sessionStorage.set(null)
                            chatRepository.deleteAllChats()
                            eventChannel.send(ChatListEvent.OnLogOutSuccess)
                        }
                        .onFailure {
                            eventChannel.send(ChatListEvent.OnLogOutError(it.toUiText()))
                        }
                }
                .onFailure {
                    eventChannel.send(ChatListEvent.OnLogOutError(it.toUiText()))
                }
        }
    }

    private fun showLogOutConfirmation() {
        _state.update { it.copy(isUserMenuOpen = false, showLogoutConfirmationDialog = true) }

    }

    private fun fetchChats() {
        viewModelScope.launch {
            chatRepository.fetchChats()
        }
    }
}