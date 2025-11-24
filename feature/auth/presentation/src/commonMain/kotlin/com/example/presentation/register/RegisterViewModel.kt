package com.example.presentation.register

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cmpcourseapp.feature.auth.presentation.generated.resources.Res
import cmpcourseapp.feature.auth.presentation.generated.resources.error_account_exists
import cmpcourseapp.feature.auth.presentation.generated.resources.error_invalid_email
import cmpcourseapp.feature.auth.presentation.generated.resources.error_invalid_password
import cmpcourseapp.feature.auth.presentation.generated.resources.error_invalid_username
import com.example.domain.EmailValidator
import com.example.domain.auth.AuthService
import com.example.domain.util.DataError
import com.example.domain.util.onFailure
import com.example.domain.util.onSuccess
import com.example.domain.validation.PasswordValidator
import com.example.presentation.util.UiText
import com.example.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val eventChannel = Channel<RegisterEvent>()
    val eventsFlow = eventChannel.receiveAsFlow()
    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(RegisterState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeValidationStates()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RegisterState()
        )

    private val isEmailValidFlow = snapshotFlow { state.value.emailTextState.text.toString() }
        .map { email -> EmailValidator.validateEmail(email) }
        .distinctUntilChanged()
    private val isUserNameValidFlow = snapshotFlow { state.value.userNameTextState.text.toString() }
        .map { name -> name.length in 3..20 }
        .distinctUntilChanged()
    private val isPasswordValidFlow = snapshotFlow { state.value.passwordTextState.text.toString() }
        .map { password -> PasswordValidator.validate(password).isValidPassword }
        .distinctUntilChanged()

    private val isRegistering = state
        .map { it.isRegistering }
        .distinctUntilChanged()

    private fun observeValidationStates() {
        combine(
            isEmailValidFlow,
            isUserNameValidFlow,
            isPasswordValidFlow,
            isRegistering

        ) { isEmailValid, isUserNameValid, isPasswordValid, isRegistering ->
            val allValid = isEmailValid && isUserNameValid && isPasswordValid
            val emailError = if (!isEmailValid) {
                UiText.MyStringResource(Res.string.error_invalid_email)
            } else null
            val userNameError = if (!isUserNameValid) {
                UiText.MyStringResource(Res.string.error_invalid_username)
            } else null
            val passwordError = if (!isPasswordValid) {
                UiText.MyStringResource(Res.string.error_invalid_password)
            } else null
            _state.update {
                it.copy(
                    emailError = emailError,
                    userNameError = userNameError,
                    passwordError = passwordError,
                    canRegister = !isRegistering && allValid
                )
            }
        }.launchIn(viewModelScope)
    }


    fun onAction(action: RegisterAction) {
        when (action) {
            RegisterAction.OnLoginClick -> Unit
            RegisterAction.OnRegisterClick -> register()
            RegisterAction.OnPasswordVisibilityClick -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }

            else -> {}
        }
    }

    private fun register() {
        if (!validateFormInputs()) {
            return
        }
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isRegistering = true
                )
            }
            val email = state.value.emailTextState.text.toString()
            val password = state.value.passwordTextState.text.toString()
            val userName = state.value.userNameTextState.text.toString()
            authService
                .register(
                    email = email,
                    password = password,
                    name = userName
                )
                .onSuccess {
                    _state.update {
                        it.copy(
                            isRegistering = false
                        )
                    }
                    eventChannel.send(RegisterEvent.Success(email))
                }
                .onFailure { error ->
                    val registrationError = when (error) {
                        DataError.Remote.CONFLICT -> UiText.MyStringResource(Res.string.error_account_exists)
                        else -> error.toUiText()
                    }
                    _state.update {
                        it.copy(
                            isRegistering = false,
                            registerError = registrationError
                        )
                    }
                }
        }
    }

    private fun validateFormInputs(): Boolean {
        val currentState = state.value
        val email = currentState.emailTextState.text.toString()
        val password = currentState.passwordTextState.text.toString()
        val userName = currentState.userNameTextState.text.toString()
        val isEmailValid = EmailValidator.validateEmail(email)
        val passwordValidationState = PasswordValidator.validate(password)
        val isUserNameValid = userName.length in 3..20
        val emailError = if (!isEmailValid) {
            UiText.MyStringResource(Res.string.error_invalid_email)
        } else null
        val userNameError = if (!isUserNameValid) {
            UiText.MyStringResource(Res.string.error_invalid_username)
        } else null
        val passwordError = if (!passwordValidationState.isValidPassword) {
            UiText.MyStringResource(Res.string.error_invalid_password)
        } else null
        _state.update {
            it.copy(
                emailError = emailError,
                userNameError = userNameError,
                passwordError = passwordError
            )
        }
        return isEmailValid && isUserNameValid && passwordValidationState.isValidPassword
    }

}