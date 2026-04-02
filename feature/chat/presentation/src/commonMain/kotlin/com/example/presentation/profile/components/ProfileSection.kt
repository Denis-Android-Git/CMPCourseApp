package com.example.presentation.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.extended
import com.example.presentation.util.DeviceConfiguration
import com.example.presentation.util.currentDeviceConfiguration

@Composable
fun ProfileSection(
    modifier: Modifier = Modifier,
    headerText: String = "Header",
    content: @Composable ColumnScope.() -> Unit
) {
    val deviceConfiguration = currentDeviceConfiguration()
    when (deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            Column(
                modifier = modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = headerText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.extended.textTertiary
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                    content = content
                )
            }
        }

        else -> {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(
                    text = headerText,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.extended.textTertiary
                )
                Column(
                    modifier = Modifier.weight(3f),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewProfileSection() {
    ProfileSection(
        headerText = "Header",
        content = {}
    )
}
