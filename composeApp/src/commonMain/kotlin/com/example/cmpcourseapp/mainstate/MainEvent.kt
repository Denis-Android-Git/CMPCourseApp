package com.example.cmpcourseapp.mainstate

sealed interface MainEvent {
    data object SessionExpired : MainEvent
}