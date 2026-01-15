package com.example.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.brand.MyHorizontalDivider
import com.example.designsystem.theme.MyTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ChatHeader(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .heightIn(max = 80.dp).padding(vertical = 20.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
        MyHorizontalDivider()
    }
}

@Preview
@Composable
fun ChatHeaderPreview() {
    MyTheme {
        ChatHeader(
            content = {
                Text(
                    text = "Chat Header",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        )
    }
}