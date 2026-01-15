package com.example.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cmpcourseapp.core.designsystem.generated.resources.reload_icon
import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.delete_for_everyone
import cmpcourseapp.feature.chat.presentation.generated.resources.retry
import cmpcourseapp.feature.chat.presentation.generated.resources.you
import com.example.designsystem.components.avatar.AvatarPhoto
import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.designsystem.components.chat.MyChatBubble
import com.example.designsystem.components.chat.TrianglePosition
import com.example.designsystem.components.dropdown.DropDownItem
import com.example.designsystem.components.dropdown.MyDropDownMenu
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import com.example.domain.models.DeliveryStatus
import com.example.presentation.model.MessageUi
import com.example.presentation.util.UiText
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.presentation.util.getChatColor

@Composable
fun MessageListItemUi(
    modifier: Modifier = Modifier,
    messageUi: MessageUi,
    onMessageLongClick: (MessageUi.LocalUserMessage) -> Unit = {},
    onDismissMessageMenu: () -> Unit = {},
    onDeleteClick: (MessageUi.LocalUserMessage) -> Unit = {},
    onRetryClick: (MessageUi.LocalUserMessage) -> Unit = {}
) {
    Box(
        modifier = modifier
    ) {
        when (messageUi) {
            is MessageUi.DateSeparator -> DateSeparatorUi(date = messageUi.date.asString(), modifier = Modifier.fillMaxWidth())
            is MessageUi.LocalUserMessage -> LocalUserMessageUi(
                messageUi = messageUi,
                modifier = modifier,
                onMessageLongClick = { onMessageLongClick(messageUi) },
                onDismissMessageMenu = onDismissMessageMenu,
                onDeleteClick = { onDeleteClick(messageUi) },
                onRetryClick = { onRetryClick(messageUi) }
            )

            is MessageUi.OtherUserMessage -> OtherUserMessageUi(
                messageUi = messageUi,
                color = getChatColor(messageUi.sender.id)
            )
        }
    }

}

@Composable
private fun DateSeparatorUi(
    date: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = date, modifier = Modifier.padding(horizontal = 40.dp), color = MaterialTheme.colorScheme.extended.textPlaceholder,
            style = MaterialTheme.typography.labelSmall
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun LocalUserMessageUi(
    messageUi: MessageUi.LocalUserMessage,
    modifier: Modifier = Modifier,
    onMessageLongClick: () -> Unit,
    onDismissMessageMenu: () -> Unit,
    onDeleteClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            MyChatBubble(
                messageContent = messageUi.content,
                userName = stringResource(Res.string.you),
                formattedTime = messageUi.formattedSentTime.asString(),
                trianglePosition = TrianglePosition.RIGHT,
                messageStatus = {
                    MessageStatusUi(
                        status = messageUi.deliveryStatus
                    )
                },
                onLongClick = {
                    onMessageLongClick()
                },
            )
            MyDropDownMenu(
                isOpen = messageUi.isMenuOpen,
                onDismissRequest = onDismissMessageMenu,
                items = listOf(
                    DropDownItem(
                        title = stringResource(Res.string.delete_for_everyone),
                        icon = Icons.Default.Delete,
                        contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                        onClick = onDeleteClick
                    )
                )
            )
        }
        if (messageUi.deliveryStatus == DeliveryStatus.FAILED) {
            IconButton(
                onClick = onRetryClick,
            ) {
                Icon(
                    vectorResource(cmpcourseapp.core.designsystem.generated.resources.Res.drawable.reload_icon),
                    contentDescription = stringResource(Res.string.retry),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun OtherUserMessageUi(
    messageUi: MessageUi.OtherUserMessage,
    modifier: Modifier = Modifier,
    color: Color
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AvatarPhoto(
            displayText = messageUi.sender.initials,
            imageUrl = messageUi.sender.imageUrl
        )
        MyChatBubble(
            messageContent = messageUi.content,
            userName = messageUi.sender.name,
            color = color,
            trianglePosition = TrianglePosition.LEFT,
            formattedTime = messageUi.formattedSentTime.asString()
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLocalUserMessage() {
    MyTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize()) {
            MessageListItemUi(
                messageUi = MessageUi.LocalUserMessage(
                    id = "1",
                    content = "Привет! Как дела, брат? 😊",
                    deliveryStatus = DeliveryStatus.FAILED,
                    formattedSentTime = UiText.DynamicString("12:30"),
                    isMenuOpen = true
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOtherUserMessage() {
    MyTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize()) {
            MessageListItemUi(
                messageUi = MessageUi.OtherUserMessage(
                    id = "22",
                    content = "Привет 👋",
                    formattedSentTime = UiText.DynamicString("12:31"),
                    sender = ChatParticipantUi(
                        id = "3",
                        name = "Alex",
                        imageUrl = "",
                        initials = "AM"
                    )
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDateSeparator() {
    MyTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MessageListItemUi(
                messageUi = MessageUi.DateSeparator(
                    id = "date_1",
                    date = UiText.DynamicString("Сегодня")
                ),
            )
        }
    }
}
