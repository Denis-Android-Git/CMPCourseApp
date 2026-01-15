package com.example.presentation.chat_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import com.example.domain.models.ConnectionState
import com.example.domain.models.DeliveryStatus
import com.example.presentation.chat_detail.components.ChatDetailHeader
import com.example.presentation.chat_detail.components.MessageBox
import com.example.presentation.chat_detail.components.MessageList
import com.example.presentation.components.ChatHeader
import com.example.presentation.model.ChatUi
import com.example.presentation.model.MessageUi
import com.example.presentation.util.UiText
import com.example.presentation.util.clearFocusOnTap
import com.example.presentation.util.currentDeviceConfiguration
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatDetailRoot(
    chatId: String?,
    isDetailPresent: Boolean,
    onBack: () -> Unit,
    viewModel: ChatDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(chatId) {
        viewModel.onAction(ChatDetailAction.OnSelectChat(chatId))
    }
    BackHandler(
        enabled = !isDetailPresent
    ) {
        viewModel.onAction(ChatDetailAction.OnSelectChat(null))
        onBack()
    }
    ChatDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        isDetailPresent = isDetailPresent
    )
}

@Composable
fun ChatDetailScreen(
    state: ChatDetailState,
    isDetailPresent: Boolean,
    onAction: (ChatDetailAction) -> Unit,
) {
    val configuration = currentDeviceConfiguration()
    val listState = rememberLazyListState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = if (!configuration.isWideScreen) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.extended.surfaceLower
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .clearFocusOnTap()
                .padding(paddingValues)
                .then(
                    if (configuration.isWideScreen) {
                        Modifier.padding(horizontal = 8.dp)
                    } else {
                        Modifier
                    }
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DynamicCornerColumn(
                    isRoundedCorner = configuration.isWideScreen,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    ChatHeader {
                        ChatDetailHeader(
                            chatUi = state.chat,
                            isDetailPresent = isDetailPresent,
                            isDropDownOpen = state.isChatOptionsOpen,
                            onChatOptionsClick = { onAction(ChatDetailAction.OnChatOptionsClick) },
                            onDisMissClick = { onAction(ChatDetailAction.OnDismissChatOptions) },
                            onManageChatClick = { onAction(ChatDetailAction.OnChatMembersClick) },
                            onLeaveChatClick = { onAction(ChatDetailAction.OnLeaveChatClick) },
                            onBackClick = { onAction(ChatDetailAction.OnBackClick) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    MessageList(
                        messages = state.messages,
                        listState = listState,
                        onMessageLongClick = { onAction(ChatDetailAction.OnMessageLongClick(it)) },
                        onMessageRetryClick = { onAction(ChatDetailAction.OnRetryClick(it)) },
                        onDismissMessageMenu = { onAction(ChatDetailAction.OnDismissMessageMenu) },
                        onDeleteClick = {
                            onAction(
                                ChatDetailAction.OnDeleteMessage(it)
                            )
                        },
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                    AnimatedVisibility(
                        visible = !configuration.isWideScreen && state.chat != null
                    ) {
                        MessageBox(
                            messageState = state.messageTextFieldState,
                            isTextInputEnabled = state.canSendMessage,
                            connectionState = state.connectionState,
                            onSendMessage = {
                                onAction(ChatDetailAction.OnSendMessageClick)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                if (configuration.isWideScreen) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                AnimatedVisibility(
                    visible = configuration.isWideScreen && state.chat != null
                ) {
                    MessageBox(
                        messageState = state.messageTextFieldState,
                        isTextInputEnabled = state.canSendMessage,
                        connectionState = state.connectionState,
                        onSendMessage = {
                            onAction(ChatDetailAction.OnSendMessageClick)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun DynamicCornerColumn(
    modifier: Modifier = Modifier,
    isRoundedCorner: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .shadow(
                if (isRoundedCorner) 4.dp else 0.dp,
                if (isRoundedCorner) RoundedCornerShape(16.dp) else RectangleShape
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = if (isRoundedCorner) RoundedCornerShape(16.dp) else RectangleShape
            )
    ) {
        content()
    }
}

@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
private fun Preview() {
    MyTheme {
        ChatDetailScreen(
            state = ChatDetailState(
                chat = ChatUi(
                    id = "accommodare",
                    localParticipant = ChatParticipantUi(
                        id = "maecenas",
                        imageUrl = "https://duckduckgo.com/?q=agam",
                        name = "Aubrey Horn", initials = "AH"
                    ),
                    remoteParticipants = listOf(
                        ChatParticipantUi(
                            id = "volutpat",
                            imageUrl = "https://www.google.com/#q=facilis",
                            name = "Antonia Cruz", initials = "AC"
                        )
                    ),
                    lastMessage = null,
                    lastMessageSenderName = "George Dominguez"

                ),
                canSendMessage = true,
                connectionState = ConnectionState.CONNECTED,
                messages = (1..8).map {
                    val isLocalUserMessage = Random.nextBoolean()
                    if (isLocalUserMessage) {
                        MessageUi.LocalUserMessage(
                            id = Uuid.random().toString(),
                            content = "Message $it",
                            deliveryStatus = DeliveryStatus.SENT,
                            formattedSentTime = UiText.DynamicString("12:00"),
                            isMenuOpen = false
                        )
                    } else {
                        MessageUi.OtherUserMessage(
                            id = Uuid.random().toString(),
                            content = "Message $it",
                            formattedSentTime = UiText.DynamicString("12:00"),
                            sender = ChatParticipantUi(
                                id = Uuid.random().toString(),
                                imageUrl = "https://www.google.com/#q=fames",
                                name = "Dean Sharpe", initials = "DS"

                            ),
                        )
                    }
                }
            ),
            onAction = {},
            isDetailPresent = false
        )
    }
}