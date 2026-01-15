package com.example.designsystem.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cmpcourseapp.core.designsystem.generated.resources.Res
import cmpcourseapp.core.designsystem.generated.resources.dismiss_dialog
import com.example.designsystem.components.buttons.MyButton
import com.example.designsystem.components.buttons.MyButtonStyle
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteDialog(
    title: String,
    description: String,
    confirmButtonText: String,
    cancelButtonText: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp).widthIn(max = 480.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.extended.textPrimary)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.extended.textSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MyButton(
                        text = cancelButtonText,
                        onClick = onCancel,
                        style = MyButtonStyle.SECONDARY
                    )
                    MyButton(
                        text = confirmButtonText,
                        onClick = onConfirm,
                        style = MyButtonStyle.DELETE_PRIMARY
                    )
                }

            }
            IconButton(
                onClick = onDismissRequest,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.dismiss_dialog),
                    tint = MaterialTheme.colorScheme.extended.textSecondary
                )
            }
        }
    }
}

@Preview
@Composable
fun DestructiveDialogPreview() {
    MyTheme(darkTheme = true) {
        DeleteDialog(
            onDismissRequest = {},
            title = "Delete",
            description = "Are you sure you want to delete this item?",
            onConfirm = {},
            confirmButtonText = "Ok",
            cancelButtonText = "Cancel",
            onCancel = {},
        )
    }
}