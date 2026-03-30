package com.example.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.no_messages
import cmpcourseapp.feature.chat.presentation.generated.resources.no_messages_subtitle
import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.designsystem.theme.MyTheme
import com.example.domain.models.DeliveryStatus
import com.example.presentation.components.EmptySection
import com.example.presentation.model.MessageUi
import com.example.presentation.util.UiText
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cmpcourseapp.feature.chat.presentation.generated.resources.retry
import com.example.designsystem.components.buttons.MyButton
import com.example.designsystem.components.buttons.MyButtonStyle

@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    messages: List<MessageUi>,
    paginationError: String?,
    isPaginationLoading: Boolean,
    onRetryPagination: () -> Unit,
    messageWitOpenMenu: MessageUi.LocalUserMessage?,
    listState: LazyListState,
    onMessageLongClick: (MessageUi.LocalUserMessage) -> Unit,
    onMessageRetryClick: (MessageUi.LocalUserMessage) -> Unit,
    onDismissMessageMenu: () -> Unit,
    onDeleteClick: (MessageUi.LocalUserMessage) -> Unit,

    ) {
    if (messages.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            EmptySection(
                title = stringResource(Res.string.no_messages),
                description = stringResource(Res.string.no_messages_subtitle),
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            state = listState,
            contentPadding = PaddingValues(16.dp),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                messages,
                key = {
                    it.id
                }
            ) { message ->
                MessageListItemUi(
                    messageUi = message,
                    onMessageLongClick = onMessageLongClick,
                    onDismissMessageMenu = onDismissMessageMenu,
                    onDeleteClick = onDeleteClick,
                    onRetryClick = onMessageRetryClick,
                    messageWitOpenMenu = messageWitOpenMenu,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                )
            }
            when {

                isPaginationLoading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                paginationError != null -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,

                            ) {
                            MyButton(
                                text = stringResource(Res.string.retry),
                                onClick = onRetryPagination,
                                style = MyButtonStyle.SECONDARY
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = paginationError,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMessageList() {
    MyTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MessageList(
                messages = listOf(
                    MessageUi.LocalUserMessage(
                        id = "1",
                        content = "Hello",
                        deliveryStatus = DeliveryStatus.SENT,
                        formattedSentTime = UiText.DynamicString("12:00"),
                    ),
                    MessageUi.OtherUserMessage(
                        id = "2",
                        content = "Hi",
                        formattedSentTime = UiText.DynamicString("12:01"),
                        sender = ChatParticipantUi(
                            name = "John Doe",
                            id = "user1",
                            imageUrl = "https://example.com/avatar.jpg",
                            initials = "JD"
                        )
                    ),
                    MessageUi.DateSeparator(
                        id = "sale", date = UiText.DynamicString("Today")
                    )
                ),
                listState = rememberLazyListState(),
                onMessageLongClick = {},
                onMessageRetryClick = {},
                onDismissMessageMenu = { },
                onDeleteClick = {},
                messageWitOpenMenu = null,
                paginationError = "Error",
                isPaginationLoading = true,
                onRetryPagination = {}
            )
        }
    }
}