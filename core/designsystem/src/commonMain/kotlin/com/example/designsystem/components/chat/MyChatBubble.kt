package com.example.designsystem.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MyChatBubble(
    modifier: Modifier = Modifier,
    messageContent: String,
    userName: String,
    formattedTime: String,
    color: Color = MaterialTheme.colorScheme.extended.surfaceHigher,
    messageStatus: @Composable (() -> Unit)? = null,
    triangleSize: Dp = 16.dp,
    trianglePosition: TrianglePosition = TrianglePosition.LEFT,
    onLongClick: (() -> Unit)? = null
) {
    val padding = 12.dp
    Column(
        modifier = modifier
            .then(
                if (onLongClick != null) {
                    Modifier.combinedClickable(
                        onLongClick = onLongClick,
                        onClick = {},
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(
                            color = MaterialTheme.colorScheme.extended.successOutline
                        )
                    )
                } else Modifier
            )
            .clip(
                ChatBubbleShape(
                    trianglePosition = trianglePosition,
                    triangleSize = triangleSize
                )
            )
            .background(color)
            .padding(
                start = if (trianglePosition == TrianglePosition.LEFT) {
                    padding + triangleSize
                } else {
                    padding
                },
                end = if (trianglePosition == TrianglePosition.RIGHT) {
                    padding + triangleSize
                } else {
                    padding
                },
                top = padding,
                bottom = padding
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = userName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.extended.textSecondary,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = formattedTime, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.extended.textSecondary
            )
        }
        Text(
            text = messageContent,
            style = MaterialTheme.typography.bodyLarge ,
            color = MaterialTheme.colorScheme.extended.textPrimary,
            modifier = Modifier.fillMaxWidth()
        )
        messageStatus?.invoke()
    }
}

@Preview
@Composable
fun MyChatBubbleLeftPreview(
) {
    MyTheme(darkTheme = true) {
        MyChatBubble(
            messageContent = "Hello, how are you? This is a test message. It should be quite long to see how it behaves. Let's see how it wraps.  ",
            userName = "John Doe",
            formattedTime = "10:30 AM",
            color = MaterialTheme.colorScheme.extended.accentGreen,
        )
    }
}

@Preview
@Composable
fun MyChatBubbleRightPreview() {
    MyTheme {
        MyChatBubble(
            messageContent = "Hello, how are you? This is a test message. It should be quite long to see how it behaves. Let's see how it wraps.  ",
            userName = "John Doe",
            formattedTime = "10:30 AM",
            trianglePosition = TrianglePosition.RIGHT
        )
    }
}