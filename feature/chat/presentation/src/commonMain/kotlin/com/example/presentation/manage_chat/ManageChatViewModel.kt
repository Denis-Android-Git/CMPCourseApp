package com.example.presentation.manage_chat

import androidx.lifecycle.ViewModel
import com.example.presentation.components.manage_chat.ManageChatAction
import com.example.presentation.components.manage_chat.ManageChatState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class ManageChatViewModel : ViewModel() {
    private val _state = MutableStateFlow(ManageChatState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<ManageChatEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: ManageChatAction) {
        when (action) {
            ManageChatAction.OnAddClick -> {}
            ManageChatAction.OnDismissClick -> {
            }

            ManageChatAction.OnPrimaryActionClick -> {}
        }
    }


}