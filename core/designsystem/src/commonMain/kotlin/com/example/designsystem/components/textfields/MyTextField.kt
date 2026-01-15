package com.example.designsystem.components.textfields

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MyTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    placeholder: String? = null,
    title: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    onFocusChanged: (Boolean) -> Unit = {},
) {
    MyTextFieldLayOut(
        modifier = modifier,
        title = title,
        isError = isError,
        supportingText = supportingText,
        enabled = enabled,
        onFocusChanged = onFocusChanged
    ) { style, interActionSource ->
        BasicTextField(
            state = state,
            enabled = enabled,
            lineLimits = if (singleLine) TextFieldLineLimits.SingleLine else TextFieldLineLimits.Default,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.extended.textPlaceholder
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            interactionSource = interActionSource,
            modifier = style,
            decorator = { innerBox ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (state.text.isEmpty() && placeholder != null) {
                        Text(
                            text = placeholder,
                            color = MaterialTheme.colorScheme.extended.textPlaceholder,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        innerBox()
                    }
                }
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun MyTextFieldPreview() {
    MyTheme {
        MyTextField(
            modifier = Modifier.width(300.dp),
            state = rememberTextFieldState("Initial text"),
            placeholder = "Placeholder",
            title = "Title",
            supportingText = "Supporting text",
            isError = false,
            singleLine = true,
            keyboardType = KeyboardType.Text,
            onFocusChanged = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
fun MyTextFieldErrorPreview() {
    MyTheme {
        MyTextField(
            modifier = Modifier.width(300.dp),
            state = rememberTextFieldState(""),
            placeholder = "Placeholder",
            title = "Title",
            supportingText = "Error message",
            isError = true,
            singleLine = true,
            keyboardType = KeyboardType.Text,
            onFocusChanged = {}
        )
    }
}