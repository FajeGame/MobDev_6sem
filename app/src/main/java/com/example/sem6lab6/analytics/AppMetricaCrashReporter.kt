package com.example.sem6lab6.analytics

import android.util.Log
import io.appmetrica.analytics.AppMetrica

class AppMetricaCrashReporter : CrashReporter {
    override fun log(message: String) {
        AppMetrica.reportEvent("crash_log", mapOf("message" to message))
        Log.d(TAG, message)
    }

    override fun recordException(throwable: Throwable) {
        AppMetrica.reportError("recorded_exception", throwable.message, throwable)
        Log.d(TAG, "recorded_exception", throwable)
    }

    private companion object {
        const val TAG = "AppMetricaCrashReporter"
    }
}
