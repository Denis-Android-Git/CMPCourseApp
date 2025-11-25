package com.example.cmpcourseapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.cmpcourseapp.mainstate.MainViewModel
import com.example.cmpcourseapp.navigation.DeepLinkListener
import com.example.cmpcourseapp.navigation.NavigationRoot
import com.example.designsystem.theme.MyTheme
import com.example.presentation.chat_list.ChatListRoute
import com.example.presentation.navigation.AuthGraphRoutes
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    onAuthChecked: () -> Unit = {},
    mainViewModel: MainViewModel = koinViewModel()
) {

    val mainState by mainViewModel.mainState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    DeepLinkListener(
        navController = navController
    )

    LaunchedEffect(mainState.isCheckingAuthStatus) {
        if (!mainState.isCheckingAuthStatus) {
            onAuthChecked()
        }
    }

    MyTheme {
        if (!mainState.isCheckingAuthStatus) {
            NavigationRoot(
                navController,
                startDestination = if (mainState.isLoggedIn) {
                    ChatListRoute
                } else {
                    AuthGraphRoutes.Graph
                },
            )
        }
    }
}