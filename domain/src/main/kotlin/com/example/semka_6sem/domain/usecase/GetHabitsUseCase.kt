package com.example.semka_6sem.domain.usecase

import com.example.semka_6sem.domain.model.HabitWithStatus
import com.example.semka_6sem.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow

// отдаёт список привычек для экрана
class GetHabitsUseCase(
    private val repository: HabitRepository,
) {
    operator fun invoke(): Flow<List<HabitWithStatus>> = repository.observeHabits()
}
