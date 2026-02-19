package com.example.presentation.manage_chat

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.error_participant_not_found
import com.example.domain.chat.ChatParticipantService
import com.example.domain.chat.ChatRepository
import com.example.domain.util.DataError
import com.example.domain.util.onFailure
import com.example.domain.util.onSuccess
import com.example.presentation.components.manage_chat.ManageChatAction
import com.example.presentation.components.manage_chat.ManageChatState
import com.example.presentation.mappers.toUi
import com.example.presentation.util.UiText
import com.example.presentation.util.toUiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ManageChatViewModel(
    private val chatRepository: ChatRepository,
    private val chatParticipantService: ChatParticipantService
) : ViewModel() {
    private var hasLoadedInitialData = false

    private val _chatId = MutableStateFlow<String?>(null)
    private val _state = MutableStateFlow(ManageChatState())

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = _chatId
        .flatMapLatest {
            if (it != null) {
                chatRepository.getActiveParticipantsByChatId(it)
            } else {
                emptyFlow()
            }

        }
        .combine(
            _state
        ) { members, currentState ->
            currentState.copy(
                existingMembers = members.map { it.toUi() }
            )

        }
        .onStart {
            if (!hasLoadedInitialData) {
                searchFlow.launchIn(viewModelScope)
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ManageChatState()
        )

    private val eventChannel = Channel<ManageChatEvent>()
    val events = eventChannel.receiveAsFlow()

    @OptIn(FlowPreview::class)
    private val searchFlow = snapshotFlow { _state.value.queryTextState.text.toString() }
        .debounce(1.seconds)
        .onEach {
            performSearch(it)
        }

    fun onAction(action: ManageChatAction) {
        when (action) {
            ManageChatAction.OnAddClick -> addMember()
            ManageChatAction.OnPrimaryActionClick -> addPeopleToChat()
            is ManageChatAction.ChatPeople.OnSelectChat -> {
                _chatId.update {
                    action.chatId
                }
            }

            else -> Unit
        }
    }

    private fun addMember() {
        state.value.currentSearchResult?.let { chatParticipantUi ->
            val isAlreadySelected = state.value.selectedMembers.any {
                it.id == chatParticipantUi.id
            }
            val isAlreadyInChat = state.value.existingMembers.any {
                it.id == chatParticipantUi.id
            }
            val updatedMembers = if (isAlreadyInChat || isAlreadySelected) {
                state.value.selectedMembers
            } else {
                state.value.selectedMembers + chatParticipantUi
            }
            state.value.queryTextState.clearText()
            _state.update {
                it.copy(
                    selectedMembers = updatedMembers,
                    canAddMember = false,
                    currentSearchResult = null
                )
            }
        }
    }


    private fun addPeopleToChat() {
        if (state.value.selectedMembers.isEmpty()) {
            return
        }
        val chatId = _chatId.value ?: return
        val selectedMembers = state.value.selectedMembers
        val selectedUserIds = selectedMembers.map { it.id }
        viewModelScope.launch {
            chatRepository.addPeopleToChat(chatId, selectedUserIds)
                .onSuccess {
                    eventChannel.send(ManageChatEvent.OnMembersAdded)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            submitError = error.toUiText()
                        )
                    }

                }

        }
    }

    private fun performSearch(value: String) {
        if (value.isBlank()) {
            _state.update {
                it.copy(
                    canAddMember = false,
                    currentSearchResult = null,
                    searchError = null
                )
            }
            return
        }
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSearching = true,
                    canAddMember = false
                )
            }
            chatParticipantService.search(value)
                .onSuccess { participant ->
                    _state.update {
                        it.copy(
                            isSearching = false,
                            currentSearchResult = participant.toUi(),
                            canAddMember = true,
                            searchError = null
                        )
                    }
                }
                .onFailure { dataErrorRemote ->
                    val error = when (dataErrorRemote) {

                        DataError.Remote.NOT_FOUND -> UiText.MyStringResource(Res.string.error_participant_not_found)

                        else -> dataErrorRemote.toUiText()
                    }
                    _state.update {
                        it.copy(
                            isSearching = false,
                            searchError = error,
                            canAddMember = false,
                            currentSearchResult = null
                        )
                    }
                }
        }
    }
}
