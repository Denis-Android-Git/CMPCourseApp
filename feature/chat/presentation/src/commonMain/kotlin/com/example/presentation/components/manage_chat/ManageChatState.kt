package com.example.presentation.components.manage_chat

import androidx.compose.foundation.text.input.TextFieldState
import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.presentation.util.UiText

data class ManageChatState(
    val queryTextState: TextFieldState = TextFieldState(),
    val isSearching: Boolean = false,
    val selectedMembers: List<ChatParticipantUi> = emptyList(),
    val existingMembers: List<ChatParticipantUi> = emptyList(),
    val canAddMember: Boolean = false,
    val currentSearchResult: ChatParticipantUi? = null,
    val searchError: UiText? = null,
    val isCreatingChat: Boolean = false,
    val createChatError: UiText? = null
)