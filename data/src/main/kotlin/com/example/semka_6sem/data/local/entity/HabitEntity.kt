package com.example.semka_6sem.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// сущность привычки в room
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val title: String,
)
