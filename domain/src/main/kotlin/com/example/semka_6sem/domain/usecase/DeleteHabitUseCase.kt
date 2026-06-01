package com.example.semka_6sem.domain.usecase

import com.example.semka_6sem.domain.repository.HabitRepository

// удаляет привычку по id
class DeleteHabitUseCase(
    private val repository: HabitRepository,
) {
    suspend operator fun invoke(id: String) {
        repository.deleteHabit(id)
    }
}
