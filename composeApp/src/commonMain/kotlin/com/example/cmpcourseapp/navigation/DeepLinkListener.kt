package com.example.cmpcourseapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavController
import androidx.navigation.NavUri
import com.example.data.logging.KermitLogger
import com.example.domain.logging.MyLogger

@Composable
fun DeepLinkListener(
    navController: NavController,
    myLogger: MyLogger = KermitLogger
) {
    DisposableEffect(Unit) {
        ExternalUriHandler.listener = { uri ->
            myLogger.debug("uri_check = DeepLinkListener: $uri")
            navController.navigate(NavUri(uri))
        }
        onDispose {
            ExternalUriHandler.listener = null
        }
    }
}