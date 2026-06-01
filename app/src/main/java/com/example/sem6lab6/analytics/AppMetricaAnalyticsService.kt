package com.example.sem6lab6.analytics

import android.util.Log
import io.appmetrica.analytics.AppMetrica

class AppMetricaAnalyticsService : AnalyticsService {
    override fun trackEvent(name: String, params: Map<String, Any>) {
        AppMetrica.reportEvent(name, params)
    }

    override fun trackError(message: String, error: Throwable?) {
        AppMetrica.reportError(message, error?.message, error)
        Log.d(TAG, "error=$message", error)
    }

    private companion object {
        const val TAG = "AppMetricaAnalytics"
    }
}
