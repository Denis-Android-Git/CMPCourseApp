package com.example.presentation.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mappers.toSerializable
import com.example.domain.auth.Crypto
import com.example.domain.auth.SessionStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ChatListScreenViewModel(
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ChatListScreenState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                /** Load initial data here **/
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ChatListScreenState()
        )

    fun onAction(action: ChatListScreenAction) {
        when (action) {
            ChatListScreenAction.Decrypt -> decrypt()
            ChatListScreenAction.Encrypt -> encrypt()
            ChatListScreenAction.Decrypt2 -> decrypt2()
        }
    }

    private fun decrypt() {
        viewModelScope.launch {
            sessionStorage.observeAuthInfo().collect {
                it?.let { authInfo ->
                    _state.update { state ->
                        state.copy(
                            userName = authInfo.user.userName,
                            email = authInfo.user.email,
                            id = authInfo.user.email,
                            hasVarifiedEmail = authInfo.user.hasVarifiedEmail,
                            accessToken = authInfo.accessToken,
                            refreshToken = authInfo.refreshToken,
                            authInfo = authInfo
                        )
                    }
                }
            }
        }
    }

    private fun decrypt2() {
        viewModelScope.launch {
            val test = Crypto.decrypt(state.value.encryptedString)
            _state.update {
                it.copy(
                    encryptedString = test
                )
            }
        }
    }

    private fun encrypt() {
        viewModelScope.launch {
            val json = Json {
                ignoreUnknownKeys = true
            }
            val serialized = json.encodeToString(state.value.authInfo?.toSerializable())
            val encryptedString = Crypto.encrypt(state.value.encryptedString)
            _state.update {
                it.copy(
                    encryptedString = encryptedString
                )
            }
        }
    }
}