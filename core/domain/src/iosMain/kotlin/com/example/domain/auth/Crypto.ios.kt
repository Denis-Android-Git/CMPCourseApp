package com.example.domain.auth

actual object Crypto {
    actual fun encrypt(text: String): String {
        return EncryptionHandler.encryptionCallback?.invoke(text).orEmpty()
    }

    actual fun decrypt(text: String): String {
        return EncryptionHandler.decryptionCallback?.invoke(text).orEmpty()
    }
}