package com.example.presentation.manage_chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.chat_members
import cmpcourseapp.feature.chat.presentation.generated.resources.save
import com.example.designsystem.components.dialogs.AdaptiveDialog
import com.example.presentation.components.manage_chat.ManageChatAction
import com.example.presentation.components.manage_chat.ManageChatScreen
import com.example.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ManageChatRoot(
    onDismiss: () -> Unit,
    onMembersAdded: () -> Unit,
    manageChatViewModel: ManageChatViewModel = koinViewModel()
) {
    val state by manageChatViewModel.state.collectAsStateWithLifecycle()


    ObserveAsEvents(manageChatViewModel.events) {
        when (it) {
            ManageChatEvent.OnMembersAdded -> {
                onMembersAdded()
            }
        }
    }
    AdaptiveDialog(
        onDismissRequest = onDismiss
    ) {
        ManageChatScreen(
            headerText = stringResource(Res.string.chat_members),
            state = state,
            onAction = {
                when (it) {
                    ManageChatAction.OnDismissClick -> onDismiss()
                    else -> Unit
                }
                manageChatViewModel.onAction(it)
            },
            primaryButtonText = stringResource(Res.string.save)
        )
    }
}