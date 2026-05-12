package com.example.sem6lab6.auth

data class AuthSession(
    val token: String,
    val userName: String,
    val provider: AuthProvider,
    val expiresAtMillis: Long
)
