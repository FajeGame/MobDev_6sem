package com.example.sem6lab6.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedAuthSessionStorage(context: Context) : AuthSessionStorage {
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        FILE_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun getSession(): AuthSession? {
        val token = sharedPreferences.getString(KEY_TOKEN, null) ?: return null
        val name = sharedPreferences.getString(KEY_NAME, null) ?: return null
        val providerName = sharedPreferences.getString(KEY_PROVIDER, null) ?: return null
        val expiresAt = sharedPreferences.getLong(KEY_EXPIRES_AT, 0L)
        val provider = runCatching { AuthProvider.valueOf(providerName) }.getOrNull() ?: return null
        return AuthSession(token, name, provider, expiresAt)
    }

    override fun saveSession(session: AuthSession) {
        sharedPreferences.edit()
            .putString(KEY_TOKEN, session.token)
            .putString(KEY_NAME, session.userName)
            .putString(KEY_PROVIDER, session.provider.name)
            .putLong(KEY_EXPIRES_AT, session.expiresAtMillis)
            .apply()
    }

    override fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    private companion object {
        const val FILE_NAME = "auth_session"
        const val KEY_TOKEN = "token"
        const val KEY_NAME = "name"
        const val KEY_PROVIDER = "provider"
        const val KEY_EXPIRES_AT = "expires_at"
    }
}
