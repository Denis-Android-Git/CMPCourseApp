package com.example.cmpcourseapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.cmpcourseapp.navigation.DeepLinkListener
import com.example.cmpcourseapp.navigation.NavigationRoot
import com.example.designsystem.theme.MyTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    DeepLinkListener(
        navController = navController
    )

    MyTheme {
        NavigationRoot(navController)
    }
}