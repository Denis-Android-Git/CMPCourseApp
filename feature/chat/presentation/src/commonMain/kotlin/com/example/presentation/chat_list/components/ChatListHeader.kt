package com.example.presentation.chat_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cmpcourseapp.core.designsystem.generated.resources.log_out_icon
import cmpcourseapp.core.designsystem.generated.resources.logo
import cmpcourseapp.core.designsystem.generated.resources.settings_icon
import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.logout
import cmpcourseapp.feature.chat.presentation.generated.resources.profile_settings
import com.example.designsystem.components.avatar.AvatarPhoto
import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.designsystem.components.dropdown.DropDownItem
import com.example.designsystem.components.dropdown.MyDropDownMenu
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import com.example.presentation.components.ChatHeader
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import cmpcourseapp.core.designsystem.generated.resources.Res as DesignSystemRes

@Composable
fun ChatListHeader(
    modifier: Modifier = Modifier,
    localParticipant: ChatParticipantUi?,
    isUserMenuOpen: Boolean, onUserAvatarClick: () -> Unit, onUserMenuDismiss: () -> Unit, onProfileSettingsClick: () -> Unit, onLogoutClick: () -> Unit
) {
    ChatHeader(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                vectorResource(DesignSystemRes.drawable.logo), contentDescription = null, tint = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "Wow chat", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.extended.textPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            localParticipant?.let {
                ProfileAvatarSection(
                    modifier = Modifier,
                    localParticipant = localParticipant,
                    isUserMenuOpen = isUserMenuOpen,
                    onClick = onUserAvatarClick,
                    onDismiss = onUserMenuDismiss,
                    onProfileSettingsClick = onProfileSettingsClick,
                    onLogoutClick = onLogoutClick
                )
            }
        }
    }
}

@Composable
fun ProfileAvatarSection(
    modifier: Modifier = Modifier,
    localParticipant: ChatParticipantUi?,
    isUserMenuOpen: Boolean,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    onProfileSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Box(modifier = modifier) {
        localParticipant?.let {
            AvatarPhoto(
                displayText = localParticipant.initials,
                imageUrl = localParticipant.imageUrl,
                onClick = onClick
            )
        }
        MyDropDownMenu(
            isOpen = isUserMenuOpen,
            onDismissRequest = onDismiss,
            items = listOf(
                DropDownItem(
                    title = stringResource(Res.string.profile_settings),
                    icon = vectorResource(DesignSystemRes.drawable.settings_icon),
                    contentColor = MaterialTheme.colorScheme.extended.textSecondary,
                    onClick = onProfileSettingsClick
                ),
                DropDownItem(
                    title = stringResource(Res.string.logout),
                    icon = vectorResource(DesignSystemRes.drawable.log_out_icon),
                    contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                    onClick = onLogoutClick
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatListHeaderPreview() {
    MyTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ChatListHeader(
                modifier = Modifier, localParticipant = ChatParticipantUi(
                    id = "1",
                    imageUrl = " https://www.gravatar.com/avatar/205e460b479e2e5b48aec07710c08d50",
                    name = "John Doe",
                    initials = "JD"
                ), isUserMenuOpen = true, onUserAvatarClick = {}, onUserMenuDismiss = {}, onProfileSettingsClick = {}, onLogoutClick = {})
        }
    }
}