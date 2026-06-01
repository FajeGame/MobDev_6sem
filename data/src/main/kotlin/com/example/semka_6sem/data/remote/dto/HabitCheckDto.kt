package com.example.semka_6sem.data.remote.dto

// dto отметки дня для firestore
data class HabitCheckDto(
    val habitId: String = "",
    val dayEpoch: Long = 0L,
)
