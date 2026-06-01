package com.example.semka_6sem.domain.repository

import kotlinx.coroutines.flow.Flow

// идентификатор пользователя яндекса для firestore
interface AuthRepository {
    val userId: Flow<String?>
    suspend fun setUserId(id: String?)
}
