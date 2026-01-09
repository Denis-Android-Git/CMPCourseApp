package com.example.presentation.util

import cmpcourseapp.feature.chat.presentation.generated.resources.Res
import cmpcourseapp.feature.chat.presentation.generated.resources.network_error
import cmpcourseapp.feature.chat.presentation.generated.resources.offline
import cmpcourseapp.feature.chat.presentation.generated.resources.online
import cmpcourseapp.feature.chat.presentation.generated.resources.reconnecting
import cmpcourseapp.feature.chat.presentation.generated.resources.unknown_error
import com.example.domain.models.ConnectionState
import com.example.domain.models.ConnectionState.*

fun ConnectionState.toUiText(): UiText {
    val resource = when (this) {
        CONNECTED -> Res.string.online
        DISCONNECTED -> Res.string.offline
        CONNECTING -> Res.string.reconnecting
        ERROR_UNKNOWN -> Res.string.unknown_error
        ERROR_NETWORK -> Res.string.network_error
    }
    return UiText.MyStringResource(resource)
}