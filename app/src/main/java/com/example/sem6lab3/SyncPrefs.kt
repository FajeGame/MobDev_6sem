package com.example.sem6lab3

import android.content.Context

object SyncPrefs {
    private const val FILE = "sync_prefs"
    private const val KEY_LAST_SYNC = "last_sync"

    fun saveLastSync(context: Context, time: Long) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_LAST_SYNC, time)
            .apply()
    }

    fun lastSync(context: Context): Long {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getLong(KEY_LAST_SYNC, 0L)
    }
}
