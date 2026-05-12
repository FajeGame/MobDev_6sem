package com.example.sem6lab6.core

import java.util.Locale

fun Int.toMovieDuration(): String {
    val hours = this / 60
    val minutes = this % 60
    return if (hours > 0) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}m"
    }
}

fun Double.toRatingText(): String = String.format(Locale.US, "%.1f", this)
