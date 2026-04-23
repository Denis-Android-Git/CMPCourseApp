package com.example.cmpcourseapp

import com.example.data.notification.IosDeviseTokenHolder

object IosDeviceTokenHolderBridge {
    fun updateToken(token: String) {
        IosDeviseTokenHolder.updateToken(token)
    }
}