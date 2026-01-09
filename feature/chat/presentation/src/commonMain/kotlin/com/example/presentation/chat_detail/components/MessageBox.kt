package com.example.presentation.chat_detail.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cmpcourseapp.core.designsystem.generated.resources.cloud_off_icon
import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.send
import cmpcourseapp.feature.chat.presentation.generated.resources.send_a_message
import com.example.designsystem.components.buttons.MyButton
import com.example.designsystem.components.textfields.MyMultilineTextField
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import com.example.domain.models.ConnectionState
import com.example.presentation.util.toUiText
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MessageBox(
    modifier: Modifier = Modifier,
    messageState: TextFieldState,
    isTextInputEnabled: Boolean,
    connectionState: ConnectionState,
    onSendMessage: () -> Unit
) {
    val isConnected = connectionState == ConnectionState.CONNECTED
    MyMultilineTextField(
        modifier = modifier.padding(4.dp),
        state = messageState,
        placeholder = stringResource(Res.string.send_a_message),
        enabled = isTextInputEnabled,
        onKeyboardActions = onSendMessage,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Send
        ),
        bottomContent = {
            Spacer(modifier = Modifier.weight(1f))
            if (!isConnected) {
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = vectorResource(cmpcourseapp.core.designsystem.generated.resources.Res.drawable.cloud_off_icon),
                        contentDescription = connectionState.toUiText().asString(),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.extended.textDisabled
                    )
                    Text(
                        text = connectionState.toUiText().asString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.extended.textDisabled
                    )
                }
            }
            MyButton(
                text = stringResource(Res.string.send),
                onClick = onSendMessage,
                enabled = isTextInputEnabled && isConnected
            )
        }
    )
}

@Preview
@Composable
fun MessageBoxPreview() {
    MyTheme {
        MessageBox(
            messageState = rememberTextFieldState(),
            isTextInputEnabled = true,
            connectionState = ConnectionState.ERROR_NETWORK,
            onSendMessage = {}
        )
    }
}