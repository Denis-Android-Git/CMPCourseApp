package com.example.presentation.chat_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.no_chat_selected
import cmpcourseapp.feature.chat.presentation.generated.resources.select_a_chat
import com.example.data.logging.KermitLogger
import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import com.example.domain.logging.MyLogger
import com.example.domain.models.ConnectionState
import com.example.domain.models.DeliveryStatus
import com.example.presentation.chat_detail.components.ChatDetailHeader
import com.example.presentation.chat_detail.components.DateChip
import com.example.presentation.chat_detail.components.MessageBannerListener
import com.example.presentation.chat_detail.components.MessageBox
import com.example.presentation.chat_detail.components.MessageList
import com.example.presentation.chat_detail.components.PaginationScrollListener
import com.example.presentation.components.ChatHeader
import com.example.presentation.components.EmptySection
import com.example.presentation.model.ChatUi
import com.example.presentation.model.MessageUi
import com.example.presentation.util.ObserveAsEvents
import com.example.presentation.util.UiText
import com.example.presentation.util.clearFocusOnTap
import com.example.presentation.util.currentDeviceConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
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
    onChatMembersClick: () -> Unit,
    viewModel: ChatDetailViewModel = koinViewModel(),
    myLogger: MyLogger = KermitLogger
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) {
        myLogger.debug("ObserveAsEvents $it")
        when (it) {
            ChatDetailEvent.OnChatLeft -> onBack()
            is ChatDetailEvent.OnError -> {
                snackbarHostState.showSnackbar(it.error.asStringAsync())
            }

            ChatDetailEvent.OnNewMessage -> {
                scope.launch {
                    listState.animateScrollToItem(0)
                }
            }
        }
    }

    LaunchedEffect(chatId) {
        viewModel.onAction(ChatDetailAction.OnSelectChat(chatId))
    }

// Use an empty state as a stub to satisfy the required argument
    val navState = rememberNavigationEventState(NavigationEventInfo.None)

    NavigationBackHandler(
        state = navState,
        isBackEnabled = !isDetailPresent,
        onBackCancelled = {
            scope.launch {
                snackbarHostState.showSnackbar("Back gesture cancelled")
            }
        },
        onBackCompleted = {
            scope.launch {
                delay(300)// Add artificial delay to prevent detail back animation from showing
                // an unselected chat the moment we go back
                viewModel.onAction(ChatDetailAction.OnSelectChat(null))
            }
            onBack()
        }
    )
    LaunchedEffect(navState.transitionState) {
        //val transitionState = navState.transitionState
//        if (transitionState is NavigationEventTransitionState.InProgress) {
//            //val progress = transitionState.latestEvent.progress
//            // Animate the back gesture progress
//        }
    }

//    BackHandler(
//        enabled = !isDetailPresent
//    ) {
//        viewModel.onAction(ChatDetailAction.OnSelectChat(null))
//        onBack()
//    }
    ChatDetailScreen(
        state = state,
        onAction = {
            when (it) {
                ChatDetailAction.OnChatMembersClick -> onChatMembersClick()
                ChatDetailAction.OnBackClick -> onBack()

                else -> Unit
            }
            viewModel.onAction(it)
        },
        isDetailPresent = isDetailPresent,
        snackbarHostState = snackbarHostState,
        listState = listState,
    )
}

@Composable
fun ChatDetailScreen(
    state: ChatDetailState,
    isDetailPresent: Boolean,
    snackbarHostState: SnackbarHostState,
    listState: LazyListState,
    onAction: (ChatDetailAction) -> Unit,
    myLogger: MyLogger = KermitLogger
) {
    val configuration = currentDeviceConfiguration()

    val realMessageItemCount = remember(state.messages) {
        state.messages.filter {
            it is MessageUi.LocalUserMessage || it is MessageUi.OtherUserMessage
        }.size
    }
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.layoutInfo.totalItemsCount
        }.filter { (firstVisibleItemIndex, totalItemsCount) ->
            firstVisibleItemIndex >= 0 && totalItemsCount > 0
        }.collect { (firstVisibleItemIndex, _) ->
            onAction(ChatDetailAction.OnFirstVisibleIndexChanged(firstVisibleItemIndex))
        }
    }
    MessageBannerListener(
        lazyListState = listState,
        messages = state.messages,
        isBannerVisible = state.bannerState.isVisible,
        onShowBanner = {
            onAction(ChatDetailAction.OnTopVisibleIndexChanged(it))
        },
        onHide = {
            onAction(ChatDetailAction.OnHideBanner)
        }

    )
    PaginationScrollListener(
        lazyListState = listState,
        itemCount = realMessageItemCount,
        isLoading = state.isPaginationLoading,
        isEndReached = state.endReached,
        onNearTop = {
            onAction(ChatDetailAction.OnScrollToTop)
        }
    )
    var headerHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
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
                    if (state.chat == null) {
                        myLogger.debug("EmptySection ${state.chat}")
                        EmptySection(
                            title = stringResource(Res.string.no_chat_selected),
                            description = stringResource(Res.string.select_a_chat),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        myLogger.debug("EmptySection ChatHeader ${state.chat}")
                        ChatHeader(
                            modifier = Modifier
                                .onSizeChanged {
                                    headerHeight = with(density) { it.height.toDp() }
                                }
                        ) {
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
                            messageWitOpenMenu = state.messageWithOpenMenu,
                            listState = listState,
                            onMessageLongClick = { onAction(ChatDetailAction.OnMessageLongClick(it)) },
                            onMessageRetryClick = { onAction(ChatDetailAction.OnRetryClick(it)) },
                            onDismissMessageMenu = { onAction(ChatDetailAction.OnDismissMessageMenu) },
                            onDeleteClick = {
                                onAction(
                                    ChatDetailAction.OnDeleteMessage(it)
                                )
                            },
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            paginationError = state.paginationError?.asString(),
                            isPaginationLoading = state.isPaginationLoading,
                            onRetryPagination = {
                                onAction(ChatDetailAction.OnRetryPaginationClick)
                            }
                        )
                        AnimatedVisibility(
                            visible = !configuration.isWideScreen
                        ) {
                            MessageBox(
                                messageState = state.messageTextFieldState,
                                isSendButtonEnabled = state.canSendMessage,
                                connectionState = state.connectionState,
                                onSendMessage = {
                                    onAction(ChatDetailAction.OnSendMessageClick)
                                },
                                modifier = Modifier.fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                            )
                        }
                    }
                }
                if (configuration.isWideScreen) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                AnimatedVisibility(
                    visible = configuration.isWideScreen && state.chat != null
                ) {
                    DynamicCornerColumn(
                        isRoundedCorner = configuration.isWideScreen
                    ) {
                        MessageBox(
                            messageState = state.messageTextFieldState,
                            isSendButtonEnabled = state.canSendMessage,
                            connectionState = state.connectionState,
                            onSendMessage = {
                                onAction(ChatDetailAction.OnSendMessageClick)
                            },
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = state.bannerState.isVisible,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = headerHeight + 16.dp),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (state.bannerState.formattedDate != null) {
                    DateChip(
                        date = state.bannerState.formattedDate.asString()
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
                if (isRoundedCorner) 8.dp else 0.dp,
                if (isRoundedCorner) RoundedCornerShape(24.dp) else RectangleShape,
                spotColor = Color.Black.copy(alpha = 0.2f)
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
                        ),
                        ChatParticipantUi(
                            id = "nibh",
                            imageUrl = "https://www.google.com/#q=tempus",
                            name = "Tara Mccoy", initials = "TM"
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
            isDetailPresent = false,
            snackbarHostState = SnackbarHostState(),
            listState = rememberLazyListState(),
        )
    }
}