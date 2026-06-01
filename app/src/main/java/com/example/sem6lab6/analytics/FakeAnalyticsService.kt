package com.example.sem6lab6.analytics

import android.util.Log

class FakeAnalyticsService : AnalyticsService {
    data class Event(
        val name: String,
        val params: Map<String, Any> = emptyMap()
    )

    data class Error(
        val message: String,
        val throwable: Throwable?
    )

    val events = mutableListOf<Event>()
    val errors = mutableListOf<Error>()

    override fun trackEvent(name: String, params: Map<String, Any>) {
        events += Event(name, params)
        safeLog("event=$name params=$params")
    }

    override fun trackError(message: String, error: Throwable?) {
        errors += Error(message, error)
        safeLog("error=$message", error)
    }

    fun clear() {
        events.clear()
        errors.clear()
    }

    private companion object {
        const val TAG = "FakeAnalytics"
    }

    private fun safeLog(message: String, throwable: Throwable? = null) {
        runCatching {
            Log.d(TAG, message, throwable)
        }
    }
}
