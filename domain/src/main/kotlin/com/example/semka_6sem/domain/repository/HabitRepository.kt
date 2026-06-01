package com.example.semka_6sem.domain.repository

import com.example.semka_6sem.domain.model.HabitWithStatus
import kotlinx.coroutines.flow.Flow

// контракт хранения привычек
interface HabitRepository {
    fun observeHabits(): Flow<List<HabitWithStatus>>
    suspend fun addHabit(title: String)
    suspend fun deleteHabit(id: String)
    suspend fun toggleToday(id: String)
    suspend fun clearAll()
    suspend fun seedDemoHabitsIfEmpty()
    suspend fun syncFromCloud()
}
