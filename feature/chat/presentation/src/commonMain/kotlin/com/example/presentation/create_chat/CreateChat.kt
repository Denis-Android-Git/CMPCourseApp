package com.example.presentation.create_chat

import ChatMembersSelectionSection
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.cancel
import cmpcourseapp.feature.chat.presentation.generated.resources.create_chat
import com.example.designsystem.components.brand.MyHorizontalDivider
import com.example.designsystem.components.buttons.MyButton
import com.example.designsystem.components.buttons.MyButtonStyle
import com.example.designsystem.components.dialogs.AdaptiveDialog
import com.example.designsystem.theme.MyTheme
import com.example.domain.models.Chat
import com.example.presentation.components.ChatMemberSearchSection
import com.example.presentation.components.ManageChatButtonRow
import com.example.presentation.components.ManageChatHeaderRow
import com.example.presentation.util.DeviceConfiguration
import com.example.presentation.util.ObserveAsEvents
import com.example.presentation.util.clearFocusOnTap
import com.example.presentation.util.currentDeviceConfiguration
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.presentation.components.manage_chat.ManageChatAction
import com.example.presentation.components.manage_chat.ManageChatScreen
import com.example.presentation.components.manage_chat.ManageChatState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateChatRoot(
    viewModel: CreateChatViewModel = koinViewModel(),
    onDismiss: () -> Unit,
    onChatCreated: (Chat) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) {
        when (it) {
            is CreateChatEvent.OnChatCreated -> onChatCreated(it.chat)
        }
    }

    AdaptiveDialog(
        onDismissRequest = onDismiss
    ) {
        ManageChatScreen(
            state = state,
            onAction = {
                when (it) {
                    ManageChatAction.OnDismissClick -> onDismiss()
                    else -> Unit
                }
                viewModel.onAction(it)
            },
            primaryButtonText = stringResource(Res.string.create_chat),
            headerText = stringResource(Res.string.create_chat)
        )
    }
}