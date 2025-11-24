package com.example.presentation.register_success

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.auth.AuthService
import com.example.domain.util.onFailure
import com.example.domain.util.onSuccess
import com.example.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterSuccessViewModel(
    private val authService: AuthService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val email = savedStateHandle.get<String>("email") ?: ""
    private val eventChannel = Channel<RegisterSuccessEvent>()
    val eventsFlow = eventChannel.receiveAsFlow()
    private val _state = MutableStateFlow(RegisterSuccessState(registeredEmail = email))
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
            initialValue = RegisterSuccessState()
        )

    fun onAction(action: RegisterSuccessAction) {
        when (action) {
            is RegisterSuccessAction.OnResendVerificationEmailClick -> resendVarification()
            else -> Unit
        }
    }

    private fun resendVarification() {
        if (state.value.isResendingEmail) {
            return
        }
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isResendingEmail = true
                )
            }
            authService.resendVarificationEmail(email = email)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isResendingEmail = false,
                        )
                    }
                    eventChannel.send(RegisterSuccessEvent.ResendEmailSuccess)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isResendingEmail = false,
                            resendVarificationError = error.toUiText()
                        )
                    }
                }
        }
    }

}