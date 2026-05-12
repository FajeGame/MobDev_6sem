package com.example.sem6lab6

import android.app.Application
import com.vk.api.sdk.VK
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig

class Sem6lab6Application : Application() {
    override fun onCreate() {
        super.onCreate()
        VK.initialize(this)
        initAppMetrica()
    }

    private fun initAppMetrica() {
        val config = AppMetricaConfig.newConfigBuilder(BuildConfig.APPMETRICA_KEY)
            .withLogs()
            .build()
        AppMetrica.activate(this, config)
        AppMetrica.enableActivityAutoTracking(this)
    }
}
