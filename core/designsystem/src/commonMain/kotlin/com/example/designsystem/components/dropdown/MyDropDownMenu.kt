package com.example.designsystem.components.dropdown

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.brand.MyHorizontalDivider
import com.example.designsystem.theme.extended
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MyDropDownMenu(
    modifier: Modifier = Modifier,
    items: List<DropDownItem>,
    isOpen: Boolean,
    onDismissRequest: () -> Unit
) {
    DropdownMenu(
        expanded = isOpen,
        shape = RoundedCornerShape(16.dp),
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface, border = BorderStroke(
            width = 1.dp, color = MaterialTheme.colorScheme.extended.surfaceOutline
        ),
        modifier = modifier
    ) {
        items.forEachIndexed { index, item ->
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            imageVector = item.icon, contentDescription = item.title,
                            tint = item.contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = item.title,
                            color = MaterialTheme.colorScheme.extended.textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                onClick = item.onClick
            )
            if (index != items.lastIndex) {
                MyHorizontalDivider()
            }
        }
    }
}

@Composable
@Preview
fun MyDropDownMenuPreview() {
    val items = listOf(
        DropDownItem(
            title = " Profile Settings",
            icon = Icons.Default.Menu,
            contentColor = MaterialTheme.colorScheme.extended.textSecondary,
            onClick = {}
        ),
        DropDownItem(
            title = " Logout",
            icon = Icons.Default.Close,
            contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
            onClick = {}
        )
    )
    Box(modifier = Modifier.fillMaxSize()) {
        MyDropDownMenu(
            items = items,
            isOpen = true,
            onDismissRequest = { }
        )
    }
}