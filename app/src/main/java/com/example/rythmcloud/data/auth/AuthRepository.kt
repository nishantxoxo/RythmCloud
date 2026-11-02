package com.example.rythmcloud.data.auth

import com.example.rythmcloud.data.remote.RetrofitInstance

class AuthRepository(
    private val tokenStore: TokenStore
) {
    private val api = RetrofitInstance.authApi

    suspend fun register(
        email: String,
        password: String,
        displayName: String?
    ): Result<User> = runCatching {
        api.register(RegisterRequest(email, password, displayName)).also {
            tokenStore.saveSession(it)
        }.user
    }

    suspend fun login(email: String, password: String): Result<User> = runCatching {
        api.login(LoginRequest(email, password)).also {
            tokenStore.saveSession(it)
        }.user
    }

    suspend fun restoreSession(): Result<User> = runCatching {
        if (tokenStore.accessToken == null) {
            error("No saved session")
        }

        api.getCurrentUser().user
    }

    suspend fun updateDisplayName(displayName: String?): Result<User> = runCatching {
        api.updateCurrentUser(UpdateProfileRequest(displayName)).user
    }

    fun logout() {
        tokenStore.clear()
    }

    suspend fun logoutRemotely() {
        tokenStore.refreshToken?.let {
            runCatching { api.logout(RefreshRequest(it)) }
        }
        tokenStore.clear()
    }
}
