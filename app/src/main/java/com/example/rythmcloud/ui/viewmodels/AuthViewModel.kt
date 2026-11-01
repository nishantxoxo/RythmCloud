package com.example.rythmcloud.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rythmcloud.data.auth.AuthRepository
import com.example.rythmcloud.data.auth.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(val user: User) : AuthState
    data class Error(val message: String) : AuthState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        restoreSession()
    }

    fun register(email: String, password: String, displayName: String?) {
        authenticate {
            repository.register(email.trim(), password, displayName?.trim()?.ifBlank { null })
        }
    }

    fun login(email: String, password: String) {
        authenticate {
            repository.login(email.trim(), password)
        }
    }

    fun logout() {
        repository.logout()
        _state.value = AuthState.Unauthenticated
    }

    private fun restoreSession() {
        viewModelScope.launch {
            repository.restoreSession()
                .onSuccess { user -> _state.value = AuthState.Authenticated(user) }
                .onFailure { _state.value = AuthState.Unauthenticated }
        }
    }

    private fun authenticate(action: suspend () -> Result<User>) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            action()
                .onSuccess { user -> _state.value = AuthState.Authenticated(user) }
                .onFailure { error ->
                    _state.value = AuthState.Error(
                        error.message ?: "Authentication failed"
                    )
                }
        }
    }
}
