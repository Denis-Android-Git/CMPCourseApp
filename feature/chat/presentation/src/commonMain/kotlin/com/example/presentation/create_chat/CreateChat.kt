package com.example.presentation.create_chat

import ChatMembersSelectionSection
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
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
import com.example.presentation.components.ChatMemberSearchSection
import com.example.presentation.components.ManageChatButtonRow
import com.example.presentation.components.ManageChatHeaderRow
import com.example.presentation.util.DeviceConfiguration
import com.example.presentation.util.clearFocusOnTap
import com.example.presentation.util.currentDeviceConfiguration
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateChatRoot(
    viewModel: CreateChatViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AdaptiveDialog(
        onDismissRequest = {
            viewModel.onAction(CreateChatAction.OnDismissClick)
        }
    ) {
        CreateChatScreen(
            state = state,
            onAction = viewModel::onAction
        )
    }
}

@Composable
fun CreateChatScreen(
    state: CreateChatState,
    onAction: (CreateChatAction) -> Unit,
) {
    var isTextFieldFocused by remember { mutableStateOf(false) }
    val imeHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    val isKeyboardVisible = imeHeight > 0
    val configuration = currentDeviceConfiguration()
    val shouldHideHeader = configuration == DeviceConfiguration.MOBILE_LANDSCAPE || (isKeyboardVisible && configuration != DeviceConfiguration.DESKTOP) || isTextFieldFocused
    Column(
        modifier = Modifier
            .clearFocusOnTap()
            .fillMaxWidth()
            .wrapContentHeight()
            .imePadding()
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        AnimatedVisibility(
            visible = !shouldHideHeader
        ) {
            Column {
                ManageChatHeaderRow(
                    title = stringResource(Res.string.create_chat),
                    onCloseClick = { onAction(CreateChatAction.OnDismissClick) },
                    modifier = Modifier.fillMaxWidth()
                )
                MyHorizontalDivider()
            }
        }
        ChatMemberSearchSection(
            queryState = state.queryTextState,
            onAddClick = { onAction(CreateChatAction.OnAddClick) },
            isSearchEnabled = state.canAddMember,
            isLoading = state.isAddingMembers,
            error = state.searchError,
            modifier = Modifier.fillMaxWidth(),
            onFocusChanged = {
                isTextFieldFocused = it
            }
        )
        MyHorizontalDivider()
        ChatMembersSelectionSection(
            memberList = state.selectedMembers,
            modifier = Modifier.fillMaxWidth(),
            searchResult = state.currentSearchResult
        )
        MyHorizontalDivider()
        ManageChatButtonRow(
            modifier = Modifier.fillMaxWidth(),
            primaryButton = {
                MyButton(
                    text = stringResource(Res.string.create_chat),
                    onClick = { onAction(CreateChatAction.OnCreateChatClick) },
                    enabled = state.selectedMembers.isNotEmpty(),
                    loading = state.isCreatingChat
                )
            },
            secondaryButton = {
                MyButton(
                    text = stringResource(Res.string.cancel),
                    onClick = { onAction(CreateChatAction.OnDismissClick) },
                    style = MyButtonStyle.SECONDARY
                )
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MyTheme {
        CreateChatScreen(
            state = CreateChatState(),
            onAction = {}
        )
    }
}