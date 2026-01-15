package com.example.designsystem.components.layouts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.brand.SuccessIcon
import com.example.designsystem.components.buttons.MyButton
import com.example.designsystem.components.buttons.MyButtonStyle
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SimpleResultLayout(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    secondaryError: String? = null,
    icon: @Composable ColumnScope.() -> Unit,
    primaryButton: @Composable () -> Unit,
    secondaryButton: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-25).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.extended.textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.extended.textSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            primaryButton()
            secondaryButton?.let {
                Spacer(modifier = Modifier.height(8.dp))
                it()
                secondaryError?.let { error ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = error, modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme. error
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
@Preview(showBackground = true)
fun SimpleSuccessLayoutPreview() {
    MyTheme(darkTheme = false) {
        SimpleResultLayout(
            title = "Success",
            description = "Your action was completed successfully.",
            secondaryError = "Optional error message",
            icon = {
                SuccessIcon()
            },
            primaryButton = {
                MyButton(
                    text = "Continue",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
            },
            secondaryButton = {
                MyButton(
                    text = "Exit",
                    onClick = {},
                    style = MyButtonStyle.SECONDARY,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}