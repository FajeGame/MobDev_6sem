package com.example.sem6lab6.analytics

import kotlin.random.Random

interface CrashReporter {
    fun log(message: String)

    fun recordException(throwable: Throwable)

    fun triggerManualCrash() {
        log("Generate crash button clicked")
        throwManualCrash()
    }
}

internal fun throwManualCrash() {
    if (Random.nextBoolean()) {
        throw NullPointerException("Manual crash from control task")
    } else {
        throw RuntimeException("Manual crash from control task")
    }
}
