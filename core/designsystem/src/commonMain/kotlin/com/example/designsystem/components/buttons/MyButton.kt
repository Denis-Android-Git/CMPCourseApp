package com.example.designsystem.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.ChirpTheme
import com.example.designsystem.theme.extended
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class MyButtonStyle {
    PRIMARY,
    SECONDARY,
    DELETE_PRIMARY,
    DELETE_SECONDARY,
    TEXT
}

@Composable
fun MyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: MyButtonStyle = MyButtonStyle.PRIMARY,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    val colors = when (style) {
        MyButtonStyle.PRIMARY -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.extended.disabledFill,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )

        MyButtonStyle.SECONDARY -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.extended.textSecondary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )

        MyButtonStyle.DELETE_PRIMARY -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
            disabledContainerColor = MaterialTheme.colorScheme.extended.disabledFill,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )

        MyButtonStyle.DELETE_SECONDARY -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.error,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )

        MyButtonStyle.TEXT -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.tertiary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )
    }

    val defaultBorder = BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.extended.disabledOutline
    )

    val border = when {
        style == MyButtonStyle.PRIMARY && !enabled -> defaultBorder
        style == MyButtonStyle.SECONDARY -> defaultBorder
        style == MyButtonStyle.DELETE_PRIMARY && !enabled -> defaultBorder
        style == MyButtonStyle.DELETE_SECONDARY -> {
            val borderColor = if (enabled) MaterialTheme.colorScheme.extended.destructiveSecondaryOutline else MaterialTheme.colorScheme.extended.disabledOutline
            BorderStroke(
                width = 1.dp,
                color = borderColor
            )
        }
        else -> null
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = colors,
        border = border
    ) {
        Box(
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(15.dp).alpha(
                    alpha = if (loading) 1f else 0f
                ), strokeWidth = 1.5.dp, color = Color.Black
            )
            Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                    8.dp,
                    androidx.compose.ui.Alignment.CenterHorizontally
                ),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier.alpha(
                    alpha = if (loading) 0f else 1f
                )
            ) {
                leadingIcon?.invoke()
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
@Preview
fun ChirpPrimaryButtonPreview() {
    ChirpTheme(
        darkTheme = true
    ) {
        MyButton(text = "Button", onClick = {})
    }
}

@Composable
@Preview
fun ChirpSecondaryButtonPreview() {
    ChirpTheme(
        darkTheme = true
    ) {
        MyButton(text = "Button", onClick = {}, style = MyButtonStyle.SECONDARY)
    }
}

@Composable
@Preview
fun ChirpDeletePrimaryButtonPreview() {
    ChirpTheme(
        darkTheme = true
    ) {
        MyButton(text = "Button", onClick = {}, style = MyButtonStyle.DELETE_PRIMARY)
    }
}

@Composable
@Preview
fun ChirpDeleteSecondaryButtonPreview() {
    ChirpTheme(
        darkTheme = true
    ) {
        MyButton(text = "Button", onClick = {}, style = MyButtonStyle.DELETE_SECONDARY)
    }
}

@Composable
@Preview
fun ChirpTextButtonPreview() {
    ChirpTheme(
        darkTheme = true
    ) {
        MyButton(text = "Button", onClick = {}, style = MyButtonStyle.TEXT)
    }
}