package com.example.designsystem.components.brand

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cmpcourseapp.core.designsystem.generated.resources.Res
import cmpcourseapp.core.designsystem.generated.resources.success_checkmark
import com.example.designsystem.theme.extended
import org.jetbrains.compose.resources.vectorResource

@Composable
fun FailureIcon(
    modifier: Modifier = Modifier

) {
    Icon(
        imageVector = Icons.Default.Close,
        contentDescription = null,
        modifier = modifier,
        tint = MaterialTheme.colorScheme.error
    )
}