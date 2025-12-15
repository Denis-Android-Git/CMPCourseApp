package com.example.presentation.chat_list_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.theme.MyTheme
import com.example.designsystem.theme.extended
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatListDetailAdaptiveLayoutRoot(
    viewModel: ChatListDetailAdaptiveLayoutViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatListDetailAdaptiveLayoutScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ChatListDetailAdaptiveLayoutScreen(
    state: ChatListDetailAdaptiveLayoutState,
    onAction: (ChatListDetailAdaptiveLayoutAction) -> Unit,
) {
    val scaffoldDirective = createNoSpacingPaneScaffoldDirective()
    val navigator = rememberListDetailPaneScaffoldNavigator(
        scaffoldDirective = scaffoldDirective
    )
    val scope = rememberCoroutineScope()
    BackHandler(
        enabled = navigator.canNavigateBack()
    ) {
        scope.launch {
            navigator.navigateBack()
        }
    }
    ListDetailPaneScaffold(
        directive = scaffoldDirective,
        value = navigator.scaffoldValue,
        modifier = Modifier.background(color = MaterialTheme.colorScheme.extended.surfaceLower),
        listPane = {
            AnimatedPane {
                LazyColumn {
                    items(100) { id ->
                        Text(
                            text = id.toString(),
                            modifier = Modifier.clickable {
                                scope.launch {
                                    onAction(ChatListDetailAdaptiveLayoutAction.OnChatClicked(id.toString()))
                                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                                }
                            }
                                .padding(16.dp)
                        )
                    }
                }
            }
        },
        detailPane = {
            AnimatedPane {
                state.selectedChatId?.let {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Detail for $it")
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun ChatListDetailAdaptiveLayoutScreenPreview() {
    MyTheme {
        ChatListDetailAdaptiveLayoutScreen(
            state = ChatListDetailAdaptiveLayoutState(),
            onAction = {}
        )
    }
}