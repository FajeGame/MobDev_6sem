package com.example.semka_6sem.fcm

import com.example.semka_6sem.util.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// обработка push от firebase cloud messaging
class HabitsFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        NotificationHelper.showReminder(this)
    }
}
