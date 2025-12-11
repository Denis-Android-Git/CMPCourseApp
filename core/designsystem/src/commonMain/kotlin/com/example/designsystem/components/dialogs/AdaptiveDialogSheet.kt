package com.example.designsystem.components.dialogs

import androidx.compose.runtime.Composable
import com.example.presentation.util.currentDeviceConfiguration

@Composable
fun AdaptiveDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    val configuration = currentDeviceConfiguration()
    if (configuration.isMobile) {
        MyBottomSheet(
            onDismissRequest = onDismissRequest,
            content = content
        )
    } else {
        DialogWrapper(
            onDismissRequest = onDismissRequest,
            content = content
        )
    }
}