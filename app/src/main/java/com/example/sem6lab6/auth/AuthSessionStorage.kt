package com.example.sem6lab6.auth

interface AuthSessionStorage {
    fun getSession(): AuthSession?

    fun saveSession(session: AuthSession)

    fun clear()
}
