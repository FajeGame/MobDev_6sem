package com.example.sem6lab6.analytics

interface AnalyticsService {
    fun trackEvent(name: String, params: Map<String, Any> = emptyMap())

    fun trackError(message: String, error: Throwable? = null)
}
