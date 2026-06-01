package com.example.semka_6sem.util

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

// инициализация firebase если есть google-services.json
object FirebaseInitializer {

    fun init(context: Context) {
        if (FirebaseApp.getApps(context).isNotEmpty()) return
        runCatching {
            FirebaseApp.initializeApp(context)
            FirebaseMessaging.getInstance().subscribeToTopic("habits_reminders")
        }
    }
}
