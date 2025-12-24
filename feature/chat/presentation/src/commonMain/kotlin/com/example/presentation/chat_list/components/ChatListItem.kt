package com.example.presentation.chat_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.group_chat
import cmpcourseapp.feature.chat.presentation.generated.resources.you
import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.designsystem.components.avatar.StackedAvatars
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import com.example.domain.models.ChatMessage
import com.example.presentation.model.ChatUi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

@Composable
fun ChatListItem(
    modifier: Modifier = Modifier,
    chatUi: ChatUi,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isGroupChat = chatUi.remoteParticipants.size > 1
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .background(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.extended.surfaceLower
                }
            )
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StackedAvatars(
                    avatars = chatUi.remoteParticipants
                )
                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (!isGroupChat) {
                            chatUi.remoteParticipants.first().name
                        } else {
                            stringResource(Res.string.group_chat)
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.extended.textPrimary,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (isGroupChat) {
                        val you = stringResource(Res.string.you)
                        val formattedNames = remember(chatUi.remoteParticipants) {
                            "$you, " + chatUi.remoteParticipants.joinToString { it.name }
                        }
                        Text(
                            text = formattedNames,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.extended.textPlaceholder,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            chatUi.lastMessage?.let {
                val message = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.extended.textSecondary,
                        )
                    ) {
                        append(chatUi.lastMessageSenderName + ": ")
                    }
                    append(chatUi.lastMessage.content)
                }
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.extended.textSecondary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }
        Box(
            modifier = Modifier
                .alpha(if (isSelected) 1f else 0f)
                .background(
                    color = MaterialTheme.colorScheme.primary
                )
                .width(4.dp)
                .fillMaxHeight()
        )
    }
}

@Preview
@Composable
fun ChatListItemPreview() {
    MyTheme(darkTheme = true) {
        ChatListItem(
            modifier = Modifier.padding(16.dp),
            chatUi = ChatUi(
                id = "1",
                lastMessage = ChatMessage(
                    content = " Hi there! How are you? I'm doing well, thanks! How about you? I'm doing well, thanks! How about you? Hi there! How are you? I'm doing well, thanks! How about you?",
                    id = "1",
                    chatId = "1",
                    createdAt = Clock.System.now(),
                    senderId = "1"
                ),
                localParticipant = ChatParticipantUi(
                    id = "1",
                    name = "John Doe",
                    imageUrl = " https://example.com/image.png",
                    initials = "JD",
                ),
                remoteParticipants = listOf(
                    ChatParticipantUi(
                        id = "2",
                        name = "Jane Doe",
                        imageUrl = " https://example.com/image.png",
                        initials = "JD",
                    ),
                    ChatParticipantUi(
                        id = "3",
                        name = "John Smith",
                        imageUrl = " https://example.com/image.png",
                        initials = "JS",
                    )
                ),
                lastMessageSenderName = " Jane Doe"
            ),
            isSelected = true,
            onClick = {})
    }
}