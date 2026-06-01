package com.example.sem6lab6.analytics

class FakeCrashReporter : CrashReporter {
    val logs = mutableListOf<String>()
    val exceptions = mutableListOf<Throwable>()
    var manualCrashTriggered = false

    override fun log(message: String) {
        logs += message
    }

    override fun recordException(throwable: Throwable) {
        exceptions += throwable
    }

    override fun triggerManualCrash() {
        manualCrashTriggered = true
        log("Generate crash button clicked")
    }

    fun clear() {
        logs.clear()
        exceptions.clear()
        manualCrashTriggered = false
    }
}
