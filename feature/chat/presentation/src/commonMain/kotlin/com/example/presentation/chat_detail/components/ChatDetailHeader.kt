package com.example.presentation.chat_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cmpcourseapp.core.designsystem.generated.resources.Res
import cmpcourseapp.core.designsystem.generated.resources.arrow_left_icon
import cmpcourseapp.core.designsystem.generated.resources.dots_icon
import cmpcourseapp.core.designsystem.generated.resources.log_out_icon
import cmpcourseapp.core.designsystem.generated.resources.users_icon
import cmpcourseapp.feature.chat.presentation.generated.resources.chat_members
import cmpcourseapp.feature.chat.presentation.generated.resources.go_back
import cmpcourseapp.feature.chat.presentation.generated.resources.leave_chat
import cmpcourseapp.feature.chat.presentation.generated.resources.open_chat_options_menu
import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.designsystem.components.buttons.MyIconButton
import com.example.designsystem.components.dropdown.DropDownItem
import com.example.designsystem.components.dropdown.MyDropDownMenu
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import com.example.domain.models.ChatMessage
import com.example.presentation.components.ChatItemHeaderRow
import com.example.presentation.model.ChatUi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

@Composable
fun ChatDetailHeader(
    modifier: Modifier = Modifier,
    chatUi: ChatUi?,
    isDropDownOpen: Boolean,
    isDetailPresent: Boolean,
    onChatOptionsClick: () -> Unit,
    onDisMissClick: () -> Unit,
    onManageChatClick: () -> Unit,
    onLeaveChatClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!isDetailPresent) {
            MyIconButton(
                onClick = onBackClick,
                content = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.arrow_left_icon),
                        contentDescription = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.go_back),
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.extended.textSecondary
                    )
                }
            )
        }

        if (chatUi != null) {
            val isGroupChat = chatUi.remoteParticipants.size > 1
            ChatItemHeaderRow(
                chatUi = chatUi,
                isGroupChat = isGroupChat,
                modifier = Modifier.weight(1f).clickable {
                    onManageChatClick()
                }
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        Box {
            MyIconButton(
                onClick = onChatOptionsClick,
                content = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.dots_icon),
                        contentDescription = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.open_chat_options_menu),
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.extended.textSecondary
                    )
                }
            )
            MyDropDownMenu(
                isOpen = isDropDownOpen,
                onDismissRequest = onDisMissClick,
                items = listOf(
                    DropDownItem(
                        title = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.chat_members),
                        icon = vectorResource(Res.drawable.users_icon),
                        contentColor = MaterialTheme.colorScheme.extended.textSecondary,
                        onClick = onManageChatClick
                    ),
                    DropDownItem(
                        title = stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.leave_chat),
                        icon = vectorResource(Res.drawable.log_out_icon),
                        contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                        onClick = onLeaveChatClick
                    )
                )
            )
        }
    }
}

@Preview
@Composable
fun PreviewChatDetailHeader() {
    MyTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ChatDetailHeader(
                chatUi = ChatUi(
                    id = "1",
                    localParticipant = ChatParticipantUi(
                        id = "1",
                        name = "John Doe",
                        imageUrl = "",
                        initials = "JD",
                    ),
                    remoteParticipants = listOf(
                        ChatParticipantUi(
                            id = "2",
                            name = "Jane Doe",
                            imageUrl = "",
                            initials = "JD",
                        ),
                        ChatParticipantUi(
                            id = "3",
                            name = "John Smith",
                            imageUrl = "",
                            initials = "JS",
                        ),
                    ),
                    lastMessage = ChatMessage(
                        id = "1",
                        content = "Hello",
                        senderId = "1",
                        chatId = "1",
                        createdAt = Clock.System.now(),
                    ),
                    lastMessageSenderName = "John Doe"
                ),
                isDropDownOpen = true,
                isDetailPresent = false,
                onChatOptionsClick = {},
                onDisMissClick = {},
                onManageChatClick = {},
                onLeaveChatClick = {},
                onBackClick = {},
            )
        }
    }
}

