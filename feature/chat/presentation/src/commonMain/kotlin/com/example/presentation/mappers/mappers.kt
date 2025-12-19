package com.example.presentation.mappers

import com.example.designsystem.components.avatar.ChatParticipantUi
import com.example.domain.models.ChatParticipant

fun ChatParticipant.toUi() = ChatParticipantUi(
    id = userId,
    imageUrl = profilePictureUrl,
    name = userName,
    initials = initials
)