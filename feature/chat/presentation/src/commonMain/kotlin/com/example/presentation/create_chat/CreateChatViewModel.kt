package com.example.presentation.create_chat

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
import com.example.presentation.mappers.toUi
import com.example.presentation.util.UiText
import com.example.presentation.util.toUiText
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class CreateChatViewModel(
    private val chatParticipantService: ChatParticipantService,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(CreateChatState())

    private val eventChannel = Channel<CreateChatEvent>()
    val events = eventChannel.receiveAsFlow()

    @OptIn(FlowPreview::class)
    private val searchFlow = snapshotFlow { _state.value.queryTextState.text.toString() }
        .debounce(1.seconds)
        .onEach {
            performSearch(it)
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

    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                searchFlow.launchIn(viewModelScope)
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CreateChatState()
        )

    fun onAction(action: CreateChatAction) {
        when (action) {
            CreateChatAction.OnAddClick -> addParticipant()
            CreateChatAction.OnCreateChatClick -> createChat()
            else -> Unit
        }
    }

    private fun createChat() {
        val idList = state.value.selectedMembers.map {
            it.id
        }
        if (idList.isEmpty()) {
            return
        }
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isCreatingChat = true,
                    canAddMember = false
                )
            }
            chatRepository.createChat(idList)
                .onSuccess { chat ->
                    _state.update {
                        it.copy(
                            isCreatingChat = false
                        )
                    }
                    eventChannel.send(CreateChatEvent.OnChatCreated(chat))
                }
                .onFailure { dataErrorRemote ->
                    _state.update {
                        it.copy(
                            createChatError = dataErrorRemote.toUiText(),
                            canAddMember = it.currentSearchResult != null && !it.isSearching,
                            isCreatingChat = false
                        )
                    }
                }
        }
    }

    private fun addParticipant() {
        state.value.currentSearchResult?.let { participantUi ->
            val isAlreadyInChat = state.value.selectedMembers.any { it.id == participantUi.id }
            if (!isAlreadyInChat) {
                _state.update {
                    it.copy(
                        selectedMembers = it.selectedMembers + participantUi,
                        canAddMember = false,
                        currentSearchResult = null
                    )
                }
                _state.value.queryTextState.clearText()
            }
        }
    }
}