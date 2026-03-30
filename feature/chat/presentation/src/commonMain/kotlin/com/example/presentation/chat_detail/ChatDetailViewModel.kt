package com.example.presentation.chat_detail

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.auth.SessionStorage
import com.example.domain.chat.ChatConnectionClient
import com.example.domain.chat.ChatRepository
import com.example.domain.message.MessageRepository
import com.example.domain.models.ChatMessage
import com.example.domain.models.ConnectionState
import com.example.domain.models.OutgoingNewMessage
import com.example.domain.util.DataErrorException
import com.example.domain.util.Paginator
import com.example.domain.util.onFailure
import com.example.domain.util.onSuccess
import com.example.presentation.mappers.toUi
import com.example.presentation.mappers.toUiList
import com.example.presentation.model.MessageUi
import com.example.presentation.util.toUiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ChatDetailViewModel(
    private val chatRepository: ChatRepository,
    sessionStorage: SessionStorage,
    private val messageRepository: MessageRepository,
    private val connectionClient: ChatConnectionClient
) : ViewModel() {

    private val _chatId = MutableStateFlow<String?>(null)
    private val eventChannel = Channel<ChatDetailEvent>()
    val events = eventChannel.receiveAsFlow()

    private var currentPaginator: Paginator<String?, ChatMessage>? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    private val chatInfoFlow = _chatId
        .onEach { chatId ->
            if (chatId != null) {
                setupPaginatorForChat(chatId)
            } else {
                currentPaginator = null
            }
        }
        .flatMapLatest {
            if (it != null) {
                chatRepository.getChatInfoById(it)
            } else emptyFlow()
        }
    private var hasLoadedInitialData = false
    private val _state = MutableStateFlow(ChatDetailState())
    private val stateWithMessages = combine(
        _state,
        chatInfoFlow,
        sessionStorage.observeAuthInfo()
    ) { currentState, chatInfo, authInfo ->
        if (authInfo == null) {
            return@combine ChatDetailState()
        }
        currentState.copy(
            chat = chatInfo.chat.toUi(authInfo.user.id),
            messages = chatInfo.messages.toUiList(authInfo.user.id)
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = _chatId
        .flatMapLatest {
            if (it != null) {
                stateWithMessages
            } else _state
        }
        .onStart {
            if (!hasLoadedInitialData) {
                observeConnectionState()
                observeChatMessages()
                observeCanSendMessage()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ChatDetailState()
        )

    fun onAction(action: ChatDetailAction) {
        when (action) {
            is ChatDetailAction.OnSelectChat -> switchChat(action.chatId)
            ChatDetailAction.OnBackClick -> {}
            ChatDetailAction.OnChatMembersClick -> {}
            ChatDetailAction.OnChatOptionsClick -> onChatOptionsClick()
            is ChatDetailAction.OnDeleteMessage -> deleteMessage(action.message)
            ChatDetailAction.OnDismissChatOptions -> onDismissChatOptions()
            ChatDetailAction.OnDismissMessageMenu -> onDismissMessageMenu()
            ChatDetailAction.OnLeaveChatClick -> onLeaveChatClick()
            is ChatDetailAction.OnMessageLongClick -> onMessageLongClick(action.message)
            is ChatDetailAction.OnRetryClick -> retryMessage(action.message)
            ChatDetailAction.OnScrollToTop -> onScrollToTop()
            ChatDetailAction.OnSendMessageClick -> sendMessage()
            ChatDetailAction.OnRetryPaginationClick -> retryPagination()
        }
    }

    private fun retryPagination() = loadNextItems()

    private fun onScrollToTop() = loadNextItems()

    private fun loadNextItems() {
        viewModelScope.launch {
            currentPaginator?.loadNextItems()
        }
    }

    private fun setupPaginatorForChat(chatId: String) {
        currentPaginator = Paginator(
            initialKey = null,
            onLoadUpdated = { isPaging ->
                _state.update {
                    it.copy(
                        isPaginationLoading = isPaging
                    )
                }
            },
            onRequest = { beforeTimStamp ->
                messageRepository.fetchMessages(chatId, beforeTimStamp)
            },
            getNextKey = { messages ->
                messages.minOfOrNull { it.createdAt }?.toString()
            },
            onError = { error ->
                if (error is DataErrorException) {
                    _state.update { it.copy(paginationError = error.error.toUiText()) }
                }
            },
            onSuccess = { messages, _ ->
                _state.update {
                    it.copy(
                        endReached = messages.isEmpty(),
                        paginationError = null
                    )
                }
            }
        )
        _state.update {
            it.copy(
                endReached = false,
                isPaginationLoading = false
            )
        }
    }

    private fun onMessageLongClick(message: MessageUi.LocalUserMessage) {
        _state.update {
            it.copy(
                messageWithOpenMenu = message
            )
        }
    }

    private fun onDismissMessageMenu() {
        _state.update {
            it.copy(
                messageWithOpenMenu = null
            )
        }
    }

    private fun deleteMessage(message: MessageUi.LocalUserMessage) {
        viewModelScope.launch {
            messageRepository.deleteMessage(message.id)
                .onFailure {
                    eventChannel.send(ChatDetailEvent.OnError(it.toUiText()))
                }
        }
    }

    private fun retryMessage(message: MessageUi.LocalUserMessage) {
        viewModelScope.launch {
            messageRepository.retrySendingMessage(message.id)
                .onFailure {
                    eventChannel.send(ChatDetailEvent.OnError(it.toUiText()))
                }
        }
    }


    private val canSendMessage = snapshotFlow {
        _state.value.messageTextFieldState.text.toString()
    }.map { it.isBlank() }
        .combine(
            connectionClient.connectionState
        ) { isBlank, connectionState ->

            !isBlank && connectionState == ConnectionState.CONNECTED
        }


    private fun observeCanSendMessage() {
        canSendMessage
            .onEach { canSend ->
                _state.update {
                    it.copy(
                        canSendMessage = canSend
                    )
                }
            }.launchIn(viewModelScope)
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun sendMessage() {
        val currentChatId = _chatId.value
        val messageText = state.value.messageTextFieldState.text.toString().trim()
        if (messageText.isBlank() || currentChatId == null) return

        viewModelScope.launch {
            val message = OutgoingNewMessage(
                chatId = currentChatId,
                messageId = Uuid.random().toString(),
                content = messageText
            )
            messageRepository.sendMessage(message)
                .onSuccess {
                    state.value.messageTextFieldState.clearText()

                }
                .onFailure {
                    eventChannel.send(ChatDetailEvent.OnError(it.toUiText()))
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeChatMessages() {
        val currentMessages = state
            .map {
                it.messages
            }
            .distinctUntilChanged()

        val newMessages = _chatId.flatMapLatest { chatId ->
            if (chatId != null) {
                messageRepository.getMessagesForChat(chatId)
            } else emptyFlow()

        }

        val isNearBottom = state.map { it.isNearBottom }
            .distinctUntilChanged()

        combine(
            currentMessages,
            newMessages,
            isNearBottom
        ) { currentMessages, newMessages, isNearBottom ->
            val lastNewId = newMessages.lastOrNull()?.message?.id
            val lastCurrentId = currentMessages.lastOrNull()?.id
            if (lastNewId != lastCurrentId && isNearBottom) {
                eventChannel.send(ChatDetailEvent.OnNewMessage)
            }
        }.launchIn(viewModelScope)
    }

    private fun observeConnectionState() {
        connectionClient.connectionState
            .onEach { connectionState ->
                if (connectionState == ConnectionState.CONNECTED) {
                    currentPaginator?.loadNextItems()
                }
                _state.update {
                    it.copy(
                        connectionState = connectionState
                    )
                }
            }.launchIn(viewModelScope)
    }

    private fun onLeaveChatClick() {
        val chatId = _chatId.value ?: return
        _state.update {
            it.copy(
                isChatOptionsOpen = false
            )
        }
        viewModelScope.launch {
            chatRepository.leaveChat(chatId)
                .onSuccess {
                    _state.value.messageTextFieldState.clearText()
                    _chatId.update { null }
                    _state.update {
                        it.copy(
                            chat = null,
                            messages = emptyList(),
                            bannerState = BannerState()
                        )
                    }
                    eventChannel.send(ChatDetailEvent.OnChatLeft)
                }
                .onFailure {
                    eventChannel.send(ChatDetailEvent.OnError(it.toUiText()))
                }
        }
    }

    private fun onDismissChatOptions() {
        _state.update {
            it.copy(
                isChatOptionsOpen = false
            )
        }
    }

    private fun onChatOptionsClick() {
        _state.update {
            it.copy(
                isChatOptionsOpen = true
            )
        }
    }

    private fun switchChat(chatId: String?) {
        _chatId.update { chatId }
        viewModelScope.launch {
            chatId?.let {
                chatRepository.fetchChatById(chatId)
            }
        }
    }
}