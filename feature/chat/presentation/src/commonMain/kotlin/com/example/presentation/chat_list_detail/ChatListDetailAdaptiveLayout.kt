package com.example.presentation.chat_list_detail

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.logging.KermitLogger
import com.example.designsystem.theme.extended
import com.example.domain.logging.MyLogger
import com.example.domain.models.Chat
import com.example.presentation.chat_detail.ChatDetailRoot
import com.example.presentation.chat_list.ChatListScreenRoot
import com.example.presentation.create_chat.CreateChatRoot
import com.example.presentation.model.ChatUi
import com.example.presentation.util.DialogScopedViewmodelScreen
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatListDetailAdaptiveLayoutRoot(
    viewModel: ChatListDetailAdaptiveLayoutViewModel = koinViewModel(),
    onConfirmLogoutClicked: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatListDetailAdaptiveLayoutScreen(
        state = state,
        onDismiss = {
            viewModel.onAction(ChatListDetailAdaptiveLayoutAction.OnDismissCurrentChatClicked)
        },
        onChatCreated = {
            viewModel.onAction(ChatListDetailAdaptiveLayoutAction.OnDismissCurrentChatClicked)
            viewModel.onAction(ChatListDetailAdaptiveLayoutAction.OnChatClicked(it.id))
        },
        onChatClicked = {
            viewModel.onAction(ChatListDetailAdaptiveLayoutAction.OnChatClicked(it?.id))
        },
        onConfirmLogoutClicked = onConfirmLogoutClicked,
        onCreateChatClicked = {
            viewModel.onAction(ChatListDetailAdaptiveLayoutAction.OnCreateChatClicked)
        },
        onProfileSettingsClicked = {
            viewModel.onAction(ChatListDetailAdaptiveLayoutAction.OnProfileSettingsClicked)
        }
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ChatListDetailAdaptiveLayoutScreen(
    state: ChatListDetailAdaptiveLayoutState,
    onDismiss: () -> Unit,
    onChatCreated: (Chat) -> Unit,
    onChatClicked: (ChatUi?) -> Unit,
    onConfirmLogoutClicked: () -> Unit,
    onCreateChatClicked: () -> Unit,
    onProfileSettingsClicked: () -> Unit,
    myLogger: MyLogger = KermitLogger
) {
    val scaffoldDirective = createNoSpacingPaneScaffoldDirective()
    val navigator = rememberListDetailPaneScaffoldNavigator(
        scaffoldDirective = scaffoldDirective
    )
    val scope = rememberCoroutineScope()
    val detailPane = navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail]

    LaunchedEffect(detailPane, state.selectedChatId) {
        if (detailPane == PaneAdaptedValue.Hidden && state.selectedChatId != null) {
            onChatClicked(null)
        }
    }

    BackHandler(
        enabled = navigator.canNavigateBack()
    ) {
        scope.launch {
            navigator.navigateBack()
        }
    }
    ListDetailPaneScaffold(
        directive = scaffoldDirective,
        value = navigator.scaffoldValue,
        modifier = Modifier.background(color = MaterialTheme.colorScheme.extended.surfaceLower),
        listPane = {
            AnimatedPane {
                ChatListScreenRoot(
                    onChatClicked = {
                        onChatClicked(it)
                        scope.launch {
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                        }
                    },
                    onConfirmLogoutClicked = onConfirmLogoutClicked,
                    onCreateChatClicked = onCreateChatClicked,
                    onProfileSettingsClicked = onProfileSettingsClicked
                )
            }
        },
        detailPane = {
            AnimatedPane {
                val listPane = navigator.scaffoldValue[ListDetailPaneScaffoldRole.List]
                state.selectedChatId?.let {
                    ChatDetailRoot(
                        chatId = it,
                        isDetailPresent = detailPane == PaneAdaptedValue.Expanded && listPane == PaneAdaptedValue.Expanded,
                        onBack = {
                            scope.launch {
                                myLogger.debug("Navigating back")
                                if (navigator.canNavigateBack()) {
                                    navigator.navigateBack()
                                }
                            }
                        }
                    )
                }
            }
        }
    )
    DialogScopedViewmodelScreen(
        isVisible = state.dialogState is DialogState.CreateChat
    ) {
        CreateChatRoot(
            onDismiss = onDismiss,
            onChatCreated = {
                onChatCreated(it)
                scope.launch {
                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                }
            }
        )
    }
}