package com.example.cmpcourseapp

import com.example.data.notification.IosDeviseTokenHolder

object IosDeviceTokenHolderBridge {
    fun updateToken(token: String) {
        println("check_ios_token - IosDeviceTokenHolderBridge.updateToken()")
        IosDeviseTokenHolder.updateToken(token)
    }
}