package com.example.presentation.create_chat

import androidx.compose.foundation.text.input.TextFieldState
import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.presentation.util.UiText

data class CreateChatState(
    val queryTextState: TextFieldState = TextFieldState(),
    val isSearching: Boolean = false,
    val selectedMembers: List<ChatParticipantUi> = emptyList(),
    val canAddMember: Boolean = false,
    val currentSearchResult: ChatParticipantUi? = null,
    val searchError: UiText? = null,
    val isCreatingChat: Boolean = false,
    val createChatError: UiText? = null
)