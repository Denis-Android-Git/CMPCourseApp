package com.example.presentation.create_chat

import androidx.compose.foundation.text.input.TextFieldState
import com.example.designsystem.components.avatar.ChatMemberUi
import com.example.presentation.util.UiText

data class CreateChatState(
    val queryTextState: TextFieldState = TextFieldState(),
    val isAddingMembers: Boolean = false,
    val isLoadingMembers: Boolean = false,
    val selectedMembers: List<ChatMemberUi> = emptyList(),
    val canAddMember: Boolean = false,
    val currentSearchResult: ChatMemberUi? = null,
    val searchError: UiText? = null,
    val isCreatingChat: Boolean = false
)