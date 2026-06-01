package com.example.sem6lab6.analytics

class CompositeCrashReporter(
    private val reporters: List<CrashReporter>
) : CrashReporter {
    override fun log(message: String) {
        reporters.forEach { reporter ->
            reporter.log(message)
        }
    }

    override fun recordException(throwable: Throwable) {
        reporters.forEach { reporter ->
            reporter.recordException(throwable)
        }
    }

    override fun triggerManualCrash() {
        log("Generate crash button clicked")
        throwManualCrash()
    }
}
