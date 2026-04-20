package com.example.sem6lab3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object TodoNotifications {
    private const val CHANNEL_ID = "todo_events"
    private const val CHANNEL_NAME = "Todo events"
    private const val SYNC_ID = 1
    private const val EXTERNAL_ID = 2

    fun createChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)
    }

    fun showSyncComplete(context: Context) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Sync complete")
            .setContentText("Tasks were synchronized")
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(SYNC_ID, notification)
    }

    fun showExternalTaskAdded(context: Context, title: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Task added from another app")
            .setContentText(title)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(EXTERNAL_ID, notification)
    }

    fun showTest(context: Context) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Test notification")
            .setContentText("Notifications work")
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(3, notification)
    }
}
