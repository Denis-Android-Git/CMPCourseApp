package com.example.presentation.chat_list_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.add
import com.example.designsystem.components.buttons.MyFloatingActionButton
import com.example.designsystem.theme.extended
import com.example.domain.models.Chat
import com.example.presentation.create_chat.CreateChatRoot
import com.example.presentation.util.DialogScopedViewmodelScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatListDetailAdaptiveLayoutRoot(
    viewModel: ChatListDetailAdaptiveLayoutViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatListDetailAdaptiveLayoutScreen(
        state = state,
        onAction = viewModel::onAction,
        onDismiss = {
            viewModel.onAction(ChatListDetailAdaptiveLayoutAction.OnDismissCurrentChatClicked)
        },
        onChatCreated = {
            viewModel.onAction(ChatListDetailAdaptiveLayoutAction.OnDismissCurrentChatClicked)
            viewModel.onAction(ChatListDetailAdaptiveLayoutAction.OnChatClicked(it.id))
        }
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ChatListDetailAdaptiveLayoutScreen(
    state: ChatListDetailAdaptiveLayoutState,
    onAction: (ChatListDetailAdaptiveLayoutAction) -> Unit,
    onDismiss: () -> Unit,
    onChatCreated: (Chat) -> Unit
) {
    val scaffoldDirective = createNoSpacingPaneScaffoldDirective()
    val navigator = rememberListDetailPaneScaffoldNavigator(
        scaffoldDirective = scaffoldDirective
    )
    val scope = rememberCoroutineScope()
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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        MyFloatingActionButton(
                            onClick = {
                                onAction(ChatListDetailAdaptiveLayoutAction.OnCreateChatClicked)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add, contentDescription = stringResource(Res.string.add)
                            )
                        }
                    }
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = it
                    ) {
                        items(100) { id ->
                            Text(
                                text = id.toString(),
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        onAction(ChatListDetailAdaptiveLayoutAction.OnCreateChatClicked)
                                        onAction(ChatListDetailAdaptiveLayoutAction.OnChatClicked(id.toString()))
                                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                                    }
                                }
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        },
        detailPane = {
            AnimatedPane {
                state.selectedChatId?.let {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Detail for $it")
                    }
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