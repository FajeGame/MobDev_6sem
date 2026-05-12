package com.example.sem6lab6.firebase

import android.content.Context

class FcmTokenStore(context: Context) {
    private val preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        preferences.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_UPDATED_AT, System.currentTimeMillis())
            .apply()
    }

    fun getToken(): String {
        return preferences.getString(KEY_TOKEN, "").orEmpty()
    }

    private companion object {
        const val FILE_NAME = "fcm_token_store"
        const val KEY_TOKEN = "fcm_token"
        const val KEY_UPDATED_AT = "fcm_token_updated_at"
    }
}
