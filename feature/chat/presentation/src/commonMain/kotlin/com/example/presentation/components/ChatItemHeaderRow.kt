package com.example.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.group_chat
import cmpcourseapp.feature.chat.presentation.generated.resources.you
import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.designsystem.components.avatar.StackedAvatars
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import com.example.domain.models.ChatMessage
import com.example.domain.models.DeliveryStatus
import com.example.presentation.model.ChatUi
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

@Composable
fun ChatItemHeaderRow(
    chatUi: ChatUi,
    isGroupChat: Boolean,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
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
}

@Preview
@Composable
fun ChatItemHeaderRowPreview() {
    MyTheme {
        ChatItemHeaderRow(
            chatUi = ChatUi(
                id = "vehicula",
                localParticipant = ChatParticipantUi(
                    id = "delicata",
                    imageUrl = "https://www.google.com/#q=quaerendum",
                    name = "Gretchen Espinoza",
                    initials = "mattis"
                ),
                remoteParticipants = listOf(
                    ChatParticipantUi(
                        id = "et",
                        imageUrl = "https://search.yahoo.com/search?p=quisque",
                        name = "Ingrid Todd", initials = "NN"
                    )
                ),
                lastMessage = ChatMessage(
                    content = "necessitatibus",
                    id = "erat", chatId = "possim",
                    createdAt = Clock.System.now(), senderId = "tractatos",
                    deliveryStatus = DeliveryStatus.SENT
                ),
                lastMessageSenderName = "Ingrid Todd"
            ), isGroupChat = false
        )
    }
}