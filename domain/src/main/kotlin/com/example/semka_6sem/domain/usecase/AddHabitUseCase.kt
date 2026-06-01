package com.example.semka_6sem.domain.usecase

import com.example.semka_6sem.domain.repository.HabitRepository

// добавляет новую привычку
class AddHabitUseCase(
    private val repository: HabitRepository,
) {
    suspend operator fun invoke(title: String) {
        repository.addHabit(title.trim())
    }
}
