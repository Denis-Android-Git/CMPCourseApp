package com.example.designsystem.components.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.designsystem.theme.MyTheme

@Composable
fun DialogWrapper(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
        ) {
            content()
        }
    }
}

@Preview
@Composable
fun DialogWrapperPreview() {
    MyTheme {
        DialogWrapper(onDismissRequest = { }) {
            Text("Dialog Content")
        }
    }
}