package com.example.semka_6sem.domain.usecase

import com.example.semka_6sem.domain.repository.HabitRepository

// переключает отметку за сегодня
class ToggleTodayUseCase(
    private val repository: HabitRepository,
) {
    suspend operator fun invoke(id: String) {
        repository.toggleToday(id)
    }
}
