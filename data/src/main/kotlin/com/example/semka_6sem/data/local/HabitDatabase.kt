package com.example.semka_6sem.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.semka_6sem.data.local.entity.HabitCheckEntity
import com.example.semka_6sem.data.local.entity.HabitEntity

@Database(
    entities = [HabitEntity::class, HabitCheckEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}
