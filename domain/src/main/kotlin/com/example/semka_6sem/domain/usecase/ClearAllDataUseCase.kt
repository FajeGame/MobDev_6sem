package com.example.semka_6sem.domain.usecase

import com.example.semka_6sem.domain.repository.AuthRepository
import com.example.semka_6sem.domain.repository.HabitRepository

// очищает данные и выходит из аккаунта
class ClearAllDataUseCase(
    private val habitRepository: HabitRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke() {
        habitRepository.clearAll()
        authRepository.setUserId(null)
    }
}
