package com.example.cmpcourseapp.mainstate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.auth.SessionStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionStorage: SessionStorage
) : ViewModel() {
    private val _mainState = MutableStateFlow(MainState())
    val mainState = _mainState.asStateFlow()

    init {
        viewModelScope.launch {
            val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
            _mainState.update {
                it.copy(
                    isCheckingAuthStatus = false,
                    isLoggedIn = authInfo != null
                )
            }
        }
    }

}