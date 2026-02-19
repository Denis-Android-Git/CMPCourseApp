package com.example.presentation.chat_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.add
import cmpcourseapp.feature.chat.presentation.generated.resources.cancel
import cmpcourseapp.feature.chat.presentation.generated.resources.do_you_want_to_logout
import cmpcourseapp.feature.chat.presentation.generated.resources.do_you_want_to_logout_desc
import cmpcourseapp.feature.chat.presentation.generated.resources.logout
import cmpcourseapp.feature.chat.presentation.generated.resources.no_chats
import cmpcourseapp.feature.chat.presentation.generated.resources.no_chats_subtitle
import com.example.designsystem.components.brand.MyHorizontalDivider
import com.example.designsystem.components.buttons.MyFloatingActionButton
import com.example.designsystem.components.dialogs.DeleteDialog
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import com.example.presentation.chat_list.components.ChatListHeader
import com.example.presentation.chat_list.components.ChatListItem
import com.example.presentation.components.EmptySection
import com.example.presentation.model.ChatUi
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatListScreenRoot(
    viewModel: ChatListScreenViewModel = koinViewModel(),
    onChatClicked: (ChatUi) -> Unit,
    onConfirmLogoutClicked: () -> Unit,
    onCreateChatClicked: () -> Unit,
    onProfileSettingsClicked: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ChatListScreenScreen(
        state = state,
        onAction = {
            when (it) {
                is ChatListScreenAction.OnChatClicked -> onChatClicked(it.chat)
                ChatListScreenAction.OnConfirmLogoutClicked -> onConfirmLogoutClicked()
                ChatListScreenAction.OnCreateChatClicked -> onCreateChatClicked()
                ChatListScreenAction.OnProfileSettingsClicked -> onProfileSettingsClicked()
                else -> Unit
            }
            viewModel.onAction(it)
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun ChatListScreenScreen(
    state: ChatListScreenState,
    onAction: (ChatListScreenAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.extended.surfaceLower,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            MyFloatingActionButton(
                onClick = {
                    onAction(ChatListScreenAction.OnCreateChatClicked)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add, contentDescription = stringResource(Res.string.add)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChatListHeader(
                localParticipant = state.localParticipant,
                isUserMenuOpen = state.isUserMenuOpen,
                onUserAvatarClick = { onAction(ChatListScreenAction.OnUsrAvatarClicked) },
                onUserMenuDismiss = { onAction(ChatListScreenAction.OnDismissUserMenu) },
                onProfileSettingsClick = { onAction(ChatListScreenAction.OnProfileSettingsClicked) },
                onLogoutClick = {
                    onAction(ChatListScreenAction.OnLogoutClicked)
                }
            )
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                state.chats.isEmpty() -> {
                    EmptySection(
                        modifier = Modifier.weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        title = stringResource(Res.string.no_chats),
                        description = stringResource(Res.string.no_chats_subtitle),
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(
                            state.chats,
                            key = { chatUi ->
                                chatUi.id
                            }
                        ) { chatUi ->
                            ChatListItem(
                                chatUi = chatUi,
                                isSelected = chatUi.id == state.selectedChatId,
                                modifier = Modifier.fillMaxWidth()
                                    .clickable {
                                        onAction(ChatListScreenAction.OnChatClicked(chatUi))
                                    }
                            )
                            MyHorizontalDivider()
                        }
                    }
                }
            }
        }
    }
    if (state.showLogoutConfirmationDialog) {
        DeleteDialog(
            title = stringResource(Res.string.do_you_want_to_logout),
            description = stringResource(Res.string.do_you_want_to_logout_desc),
            onDismissRequest = { onAction(ChatListScreenAction.OnDismissLogoutDialog) },
            onConfirm = {
                onAction(ChatListScreenAction.OnLogoutClicked)
            },
            onCancel = { onAction(ChatListScreenAction.OnDismissLogoutDialog) },
            confirmButtonText = stringResource(Res.string.logout),
            cancelButtonText = stringResource(Res.string.cancel)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MyTheme {
        ChatListScreenScreen(
            state = ChatListScreenState(),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}