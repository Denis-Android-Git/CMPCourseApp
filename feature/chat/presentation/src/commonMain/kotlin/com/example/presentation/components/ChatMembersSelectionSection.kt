import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.avatar.AvatarPhoto
import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.designsystem.theme.extended
import com.example.presentation.util.DeviceConfiguration
import com.example.presentation.util.currentDeviceConfiguration

@Composable
fun ColumnScope.ChatMembersSelectionSection(
    modifier: Modifier = Modifier,
    memberList: List<ChatParticipantUi>,
    searchResult: ChatParticipantUi? = null
) {
    val configuration = currentDeviceConfiguration()
    val rootHeightModifier = when (configuration) {
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            Modifier
                .animateContentSize()
                .heightIn(min = 200.dp, max = 300.dp)
        }

        else -> {
            Modifier
                .weight(1f)
        }
    }
    Box(
        modifier = rootHeightModifier.then(modifier)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            searchResult?.let {
                item {
                    ChatMemberItem(
                        chatParticipantUi = searchResult
                    )
                }
            }
            if (memberList.isNotEmpty() && searchResult == null) {
                items(memberList, key = { it.id }) { member ->
                    ChatMemberItem(
                        chatParticipantUi = member
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMemberItem(
    modifier: Modifier = Modifier,
    chatParticipantUi: ChatParticipantUi
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AvatarPhoto(
            displayText = chatParticipantUi.initials,
            imageUrl = chatParticipantUi.imageUrl,
        )
        Text(
            text = chatParticipantUi.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.extended.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

}

