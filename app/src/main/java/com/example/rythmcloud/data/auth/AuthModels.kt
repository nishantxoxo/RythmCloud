package com.example.rythmcloud.data.auth

data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)

data class RefreshRequest(val refreshToken: String)

data class UpdateProfileRequest(val displayName: String?)

data class CurrentUserResponse(
    val user: User
)

data class User(
    val id: String,
    val email: String,
    val displayName: String?,
    val role: String
)
