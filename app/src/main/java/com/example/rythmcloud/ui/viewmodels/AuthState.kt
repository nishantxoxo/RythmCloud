package com.example.rythmcloud.ui.viewmodels

import com.example.rythmcloud.data.auth.User

sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(
        val user: User,
        val isUpdatingProfile: Boolean = false,
        val profileError: String? = null
    ) : AuthState
    data class Error(val message: String) : AuthState
}
