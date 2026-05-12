package com.example.sem6lab6.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.sem6lab6.MainActivity
import com.example.sem6lab6.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        FcmTokenStore(this).saveToken(token)
        Log.d(TAG, "FCM Token: $token")
        Log.d(TAG, "Current user: ${FirebaseAuth.getInstance().currentUser?.uid}")
        updateFirestoreToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "onMessageReceived called!")
        Log.d(TAG, "Message from: ${remoteMessage.from}")

        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            Log.d(TAG, "Data payload: $data")
        }

        val notification = remoteMessage.notification
        Log.d(TAG, "Has notification object: ${notification != null}")
        if (notification != null) {
            Log.d(TAG, "Notification - title: ${notification.title}, body: ${notification.body}")
        }

        val title = data["title"]
            ?: remoteMessage.notification?.title
            ?: "Filmi from Nikitka"
        val body = data["body"]
            ?: remoteMessage.notification?.body
            ?: "У тебя новое уведомление"

        Log.d(TAG, "Showing notification with title='$title', body='$body'")
        showNotification(title, body, data)
    }

    private fun showNotification(
        title: String,
        body: String,
        data: Map<String, String>
    ) {
        try {
            createNotificationChannel()

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                data.forEach { (key, value) -> putExtra(key, value) }
            }
            val pendingIntent = PendingIntent.getActivity(
                this,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getColor(R.color.notification_color))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build()

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = System.currentTimeMillis().toInt()
            notificationManager.notify(notificationId, notification)
            Log.d(TAG, "Notification posted with id=$notificationId")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Основные уведомления",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun updateFirestoreToken(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .update(
                mapOf(
                    "fcmToken" to token,
                    "updatedAt" to System.currentTimeMillis()
                )
            )
            .addOnFailureListener { error ->
                Log.w(TAG, "Failed to update FCM token in Firestore", error)
            }
    }

    private companion object {
        const val TAG = "PushMessagingService"
        const val CHANNEL_ID = "filmi_push_channel"
    }
}
