package com.example.semka_6sem.data.local.entity

import androidx.room.Entity

// отметка выполнения за конкретный день
@Entity(
    tableName = "habit_checks",
    primaryKeys = ["habitId", "dayEpoch"],
)
data class HabitCheckEntity(
    val habitId: String,
    val dayEpoch: Long,
)
