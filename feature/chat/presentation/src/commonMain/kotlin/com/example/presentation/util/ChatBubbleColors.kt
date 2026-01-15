package com.example.presentation.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.designsystem.theme.extended

@Composable
fun getChatColor(id: String): Color {
    val colorPool = with(MaterialTheme.colorScheme.extended) {
        listOf(
            cakeViolet,
            cakeGreen,
            cakeBlue,
            cakePink,
            cakeOrange,
            cakeYellow,
            cakeTeal,
            cakePurple,
            cakeRed,
            cakeMint
        )
    }
    val index = id.hashCode().toUInt() % colorPool.size.toUInt()
    return colorPool[index.toInt()]
}