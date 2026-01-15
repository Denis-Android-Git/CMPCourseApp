package com.example.presentation.chat_detail.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cmpcourseapp.core.designsystem.generated.resources.Res
import cmpcourseapp.core.designsystem.generated.resources.check_icon
import cmpcourseapp.core.designsystem.generated.resources.loading_icon
import cmpcourseapp.feature.chat.presentation.generated.resources.failed
import cmpcourseapp.feature.chat.presentation.generated.resources.sending
import cmpcourseapp.feature.chat.presentation.generated.resources.sent
import com.example.designsystem.theme.extended
import com.example.domain.models.DeliveryStatus
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MessageStatusUi(
    modifier: Modifier = Modifier,
    status: DeliveryStatus
) {
    val (text, icon, color) = when (status) {
        DeliveryStatus.SENT -> Triple(
            stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.sent),
            vectorResource(Res.drawable.check_icon),
            MaterialTheme.colorScheme.extended.textTertiary
        )

        DeliveryStatus.FAILED -> Triple(
            stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.failed),
            Icons.Default.Close,
            MaterialTheme.colorScheme.error
        )

        DeliveryStatus.SENDING -> Triple(
            stringResource(cmpcourseapp.feature.chat.presentation.generated.resources.Res.string.sending),
            vectorResource(Res.drawable.loading_icon),
            MaterialTheme.colorScheme.extended.textDisabled
        )
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = text, tint = color, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = color, style = MaterialTheme.typography.labelSmall)
    }
}
@Preview
@Composable
fun MessageStatusUiPreview() {
    MessageStatusUi(status = DeliveryStatus.SENDING)
}