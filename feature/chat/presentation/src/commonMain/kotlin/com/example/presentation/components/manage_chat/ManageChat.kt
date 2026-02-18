package com.example.presentation.components.manage_chat

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
import androidx.compose.ui.tooling.preview.Preview
import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.cancel
import com.example.designsystem.components.brand.MyHorizontalDivider
import com.example.designsystem.components.buttons.MyButton
import com.example.designsystem.components.buttons.MyButtonStyle
import com.example.designsystem.theme.MyTheme
import com.example.presentation.components.ChatMemberSearchSection
import com.example.presentation.components.ManageChatButtonRow
import com.example.presentation.components.ManageChatHeaderRow
import com.example.presentation.util.DeviceConfiguration
import com.example.presentation.util.clearFocusOnTap
import com.example.presentation.util.currentDeviceConfiguration
import org.jetbrains.compose.resources.stringResource

@Composable
fun ManageChatScreen(
    headerText: String,
    primaryButtonText: String,
    state: ManageChatState,
    onAction: (ManageChatAction) -> Unit,
) {
    var isTextFieldFocused by remember { mutableStateOf(false) }
    val imeHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    val isKeyboardVisible = imeHeight > 0
    val configuration = currentDeviceConfiguration()
    val shouldHideHeader =
        configuration == DeviceConfiguration.MOBILE_LANDSCAPE || (isKeyboardVisible && configuration != DeviceConfiguration.DESKTOP)
                || isTextFieldFocused
    Column(
        modifier = Modifier
            .clearFocusOnTap()
            .fillMaxWidth()
            .wrapContentHeight()
            .imePadding()
            .background(color = MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
    ) {
        AnimatedVisibility(
            visible = !shouldHideHeader
        ) {
            Column {
                ManageChatHeaderRow(
                    title = headerText,
                    onCloseClick = { onAction(ManageChatAction.OnDismissClick) },
                    modifier = Modifier.fillMaxWidth()
                )
                MyHorizontalDivider()
            }
        }
        ChatMemberSearchSection(
            queryState = state.queryTextState,
            onAddClick = { onAction(ManageChatAction.OnAddClick) },
            isSearchEnabled = state.canAddMember,
            isLoading = state.isSearching,
            error = state.searchError,
            modifier = Modifier.fillMaxWidth(),
            onFocusChanged = {
                isTextFieldFocused = it
            }
        )
        MyHorizontalDivider()
        ChatMembersSelectionSection(
            existingMembers = state.existingMembers,
            memberList = state.selectedMembers,
            modifier = Modifier.fillMaxWidth(),
            searchResult = state.currentSearchResult
        )
        MyHorizontalDivider()
        ManageChatButtonRow(
            modifier = Modifier.fillMaxWidth(),
            error = state.submitError?.asString(),
            primaryButton = {
                MyButton(
                    text = primaryButtonText,
                    onClick = { onAction(ManageChatAction.OnPrimaryActionClick) },
                    enabled = state.selectedMembers.isNotEmpty(),
                    loading = state.isSubmitting
                )
            },
            secondaryButton = {
                MyButton(
                    text = stringResource(Res.string.cancel),
                    onClick = { onAction(ManageChatAction.OnDismissClick) },
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
        ManageChatScreen(
            state = ManageChatState(),
            onAction = {},
            primaryButtonText = "Push",
            headerText = " Create Chat"
        )
    }
}