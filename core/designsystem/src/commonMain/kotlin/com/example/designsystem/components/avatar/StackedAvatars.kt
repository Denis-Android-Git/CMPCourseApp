package com.example.designsystem.components.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.theme.MyTheme

@Composable
fun StackedAvatars(
    modifier: Modifier = Modifier,
    avatars: List<ChatParticipantUi>,
    size: AvatarSize = AvatarSize.SMALL,
    maxVisible: Int = 2,
    overlapPercentage: Float = 0.4f
) {
    val overLapOffset = -(size.dp * overlapPercentage)
    val visibleAvatars = avatars.take(maxVisible)
    val remainingCount = (avatars.size - maxVisible).coerceAtLeast(0)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(overLapOffset),
        verticalAlignment = Alignment.CenterVertically
    ) {
        visibleAvatars.forEach { avatarUi ->
            AvatarPhoto(
                size = size,
                displayText = avatarUi.initials,
                imageUrl = avatarUi.imageUrl
            )
        }
        if (remainingCount > 0) {
            AvatarPhoto(
                size = size,
                displayText = "$remainingCount+",
                textColor = MaterialTheme.colorScheme.primary
            )
        }
    }

}

@Preview
@Composable
fun StackedAvatarsPreview() {
    MyTheme {
        StackedAvatars(
            modifier = Modifier,
            avatars = listOf(
                ChatParticipantUi(
                    imageUrl = "https://example.com/avatar1.png",
                    id = 1.toString(),
                    name = "Ivan",
                    initials = "ID"
                ),
                ChatParticipantUi(
                    imageUrl = "https://example.com/avatar2.png",
                    id = 2.toString(),
                    name = "Den",
                    initials = "DD"
                ),
                ChatParticipantUi(
                    imageUrl = "https://example.com/avatar3.png",
                    id = 3.toString(),
                    name = "Ilya",
                    initials = "IK"

                ),
                ChatParticipantUi(
                    imageUrl = "https://example.com/avatar4.png",
                    id = 4.toString(),
                    name = "Kostya",
                    initials = "KK"
                )
            ),
            size = AvatarSize.LARGE
        )
    }
}