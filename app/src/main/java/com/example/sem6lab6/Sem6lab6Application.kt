package com.example.sem6lab6

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.vk.api.sdk.VK
import com.example.sem6lab6.firebase.FcmTokenStore
import com.google.firebase.messaging.FirebaseMessaging
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig

class Sem6lab6Application : Application() {
    override fun onCreate() {
        super.onCreate()
        VK.initialize(this)
        createNotificationChannel()
        loadFcmToken()
        initAppMetrica()
    }

    private fun initAppMetrica() {
        val config = AppMetricaConfig.newConfigBuilder(BuildConfig.APPMETRICA_KEY)
            .withLogs()
            .build()
        AppMetrica.activate(this, config)
        AppMetrica.enableActivityAutoTracking(this)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            getString(R.string.default_notification_channel_id),
            "Основные уведомления",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun loadFcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            FcmTokenStore(this).saveToken(token)
        }
    }
}
