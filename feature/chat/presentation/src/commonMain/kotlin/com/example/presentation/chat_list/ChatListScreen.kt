package com.example.presentation.chat_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.components.buttons.MyButton
import com.example.designsystem.theme.MyTheme
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatListScreenRoot(
    viewModel: ChatListScreenViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatListScreenScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ChatListScreenScreen(
    state: ChatListScreenState,
    onAction: (ChatListScreenAction) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column {
            Text(text = "ChatListScreen", color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(20.dp))
            MyButton(
                text = "Decrypt",
                onClick = {
                    onAction(ChatListScreenAction.Decrypt)
                }
            )
            MyButton(
                text = "Encrypt",
                onClick = {
                    onAction(ChatListScreenAction.Encrypt)
                }
            )
            MyButton(
                text = "Decrypt 2",
                onClick = {
                    onAction(ChatListScreenAction.Decrypt2)
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = state.userName, color = MaterialTheme.colorScheme.primary)
            Text(text = state.email, color = MaterialTheme.colorScheme.primary)
            Text(text = state.hasVarifiedEmail.toString(), color = MaterialTheme.colorScheme.primary)
            Text(text = state.accessToken, color = MaterialTheme.colorScheme.primary)
            Text(text = state.refreshToken, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(20.dp))

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = state.encryptedString, color = MaterialTheme.colorScheme.primary)

        }
    }
}

@Serializable
data object ChatListRoute

@Preview
@Composable
private fun Preview() {
    MyTheme {
        ChatListScreenScreen(
            state = ChatListScreenState(),
            onAction = {}
        )
    }
}