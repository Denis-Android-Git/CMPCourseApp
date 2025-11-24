package com.example.domain.auth

expect object Crypto {
    fun encrypt(text: String): String
    fun decrypt(text: String): String
}

object EncryptionHandler {
    var encryptionCallback: ((String) -> String)? = null
    var decryptionCallback: ((String) -> String)? = null
    fun encrypt(callback: (String) -> String) {
        encryptionCallback = callback
    }
    fun decrypt(callback: (String) -> String) {
        decryptionCallback = callback
    }
}