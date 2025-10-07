package com.example.presentation.util

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

sealed interface UiText {
    data class DynamicString(val text: String) : UiText
    class MyStringResource(
        val id: StringResource,
        val args: Array<Any> = arrayOf()
    ) : UiText

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> text
            is MyStringResource -> stringResource(id, *args)
        }
    }

    suspend fun asStringAsync(): String {
        return when (this) {
            is DynamicString -> text
            is MyStringResource -> getString(id, *args)
        }
    }
}
