package com.example.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.data.auth.dto.AuthInfoSerializable
import com.example.data.mappers.toDomain
import com.example.data.mappers.toSerializable
import com.example.domain.auth.AuthInfo
import com.example.domain.auth.Crypto
import com.example.domain.auth.SessionStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlin.io.encoding.ExperimentalEncodingApi

class DataStoreSessionStorage(
    private val dataStore: DataStore<Preferences>
) : SessionStorage {
    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val authInfoKey = stringPreferencesKey("KEY_AUTH_INFO")

    @OptIn(ExperimentalEncodingApi::class)
    override fun observeAuthInfo(): Flow<AuthInfo?> {
        return dataStore.data.map { prefs ->
            prefs[authInfoKey]?.let {
                val decryptedString = Crypto.decrypt(it)
                val authInfoSerializable: AuthInfoSerializable = json.decodeFromString(decryptedString)
                authInfoSerializable.toDomain()
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun set(info: AuthInfo?) {
        if (info == null) {
            dataStore.edit {
                it.remove(authInfoKey)
            }
            return
        }
        val serialized = json.encodeToString(info.toSerializable())
        val encryptedString = Crypto.encrypt(serialized)
        dataStore.edit {
            it[authInfoKey] = encryptedString
        }
    }
}