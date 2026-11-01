package com.example.rythmcloud.data.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenStore(context: Context) {
    private val preferences = EncryptedSharedPreferences.create(
        context,
        "auth_session",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    val accessToken: String?
        get() = preferences.getString(KEY_ACCESS_TOKEN, null)

    fun saveSession(response: AuthResponse) {
        preferences.edit()
            .putString(KEY_ACCESS_TOKEN, response.accessToken)
            .putString(KEY_USER_ID, response.user.id)
            .putString(KEY_EMAIL, response.user.email)
            .putString(KEY_ROLE, response.user.role)
            .apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_EMAIL = "email"
        const val KEY_ROLE = "role"
    }
}
