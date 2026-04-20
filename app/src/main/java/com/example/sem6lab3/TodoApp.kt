package com.example.sem6lab3

import android.app.Application
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat

class TodoApp : Application() {
    private val receiver = NetworkChangeReceiver()

    override fun onCreate() {
        super.onCreate()
        TodoNotifications.createChannel(this)
        ContextCompat.registerReceiver(
            this,
            receiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION),
            ContextCompat.RECEIVER_EXPORTED
        )
        TodoSyncScheduler.schedule(this)
    }
}
