package com.example.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.presentation.chat_list_detail.ChatListDetailAdaptiveLayoutRoot
import kotlinx.serialization.Serializable

sealed interface ChatGraphRoutes {
    @Serializable
    object Graph : ChatGraphRoutes

    @Serializable
    data class ChatListDetailRoute(
        val chatId: String? = null
    ) : ChatGraphRoutes
}

fun NavGraphBuilder.chatGraph(
    navController: NavController,
    onLogout: () -> Unit
) {
    navigation<ChatGraphRoutes.Graph>(
        startDestination = ChatGraphRoutes.ChatListDetailRoute()
    ) {
        composable<ChatGraphRoutes.ChatListDetailRoute>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "chirp://chat_detail/{chatId}"
                }
            )
        ) { backStack ->
            val chatId = backStack.toRoute<ChatGraphRoutes.ChatListDetailRoute>().chatId
            ChatListDetailAdaptiveLayoutRoot(
                initialChatId = chatId,
                onConfirmLogoutClicked = onLogout
            )
        }
    }
}