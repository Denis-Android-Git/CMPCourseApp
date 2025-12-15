package com.example.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.presentation.chat_list_detail.ChatListDetailAdaptiveLayoutRoot
import kotlinx.serialization.Serializable

sealed interface ChatGraphRoutes {
    @Serializable
    object Graph : ChatGraphRoutes

    @Serializable
    object ChatListDetailRoute : ChatGraphRoutes
}

fun NavGraphBuilder.chatGraph(
    navController: NavController
) {
    navigation<ChatGraphRoutes.Graph>(
        startDestination = ChatGraphRoutes.ChatListDetailRoute
    ) {
        composable<ChatGraphRoutes.ChatListDetailRoute> {
            ChatListDetailAdaptiveLayoutRoot()
        }
    }
}