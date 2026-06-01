package com.example.semka_6sem.domain.util

// считает серию дней подряд от сегодняшнего дня назад
object StreakCalculator {

    fun calculate(checkDays: Set<Long>, todayEpoch: Long): Int {
        if (checkDays.isEmpty()) return 0
        var streak = 0
        var day = todayEpoch
        while (checkDays.contains(day)) {
            streak++
            day--
        }
        return streak
    }
}
