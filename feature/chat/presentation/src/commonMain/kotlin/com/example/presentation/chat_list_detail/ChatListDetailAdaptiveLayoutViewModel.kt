package com.example.presentation.chat_list_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ChatListDetailAdaptiveLayoutViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ChatListDetailAdaptiveLayoutState())
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
            initialValue = ChatListDetailAdaptiveLayoutState()
        )

    fun onAction(action: ChatListDetailAdaptiveLayoutAction) {
        when (action) {
            is ChatListDetailAdaptiveLayoutAction.OnChatClicked -> {
                _state.update {
                    it.copy(
                        selectedChatId = action.chatId
                    )
                }
            }

            ChatListDetailAdaptiveLayoutAction.OnCreateChatClicked -> {
                _state.update {
                    it.copy(
                        dialogState = DialogState.CreateChat
                    )
                }
            }

            ChatListDetailAdaptiveLayoutAction.OnDismissCurrentChatClicked -> {
                _state.update {
                    it.copy(
                        dialogState = DialogState.Hidden
                    )
                }
            }

            ChatListDetailAdaptiveLayoutAction.OnManageChatClicked -> {
                state.value.selectedChatId?.let { id ->
                    _state.update {
                        it.copy(
                            dialogState = DialogState.ManageChat(chatId = id)
                        )
                    }
                }
            }

            ChatListDetailAdaptiveLayoutAction.OnProfileSettingsClicked -> {
                _state.update {
                    it.copy(
                        dialogState = DialogState.Profile
                    )
                }
            }
        }
    }

}