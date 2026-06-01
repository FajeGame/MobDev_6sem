package com.example.semka_6sem.domain.model

// привычка плюс отметка за сегодня и серия дней
data class HabitWithStatus(
    val habit: Habit,
    val doneToday: Boolean,
    val streak: Int,
)
