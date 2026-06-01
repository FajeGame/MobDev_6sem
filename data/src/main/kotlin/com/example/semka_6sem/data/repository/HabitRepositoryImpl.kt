package com.example.semka_6sem.data.repository

import com.example.semka_6sem.data.local.HabitDao
import com.example.semka_6sem.data.local.entity.HabitCheckEntity
import com.example.semka_6sem.data.mapper.HabitMapper
import com.example.semka_6sem.data.remote.FirestoreHabitDataSource
import com.example.semka_6sem.domain.model.Habit
import com.example.semka_6sem.domain.model.HabitWithStatus
import com.example.semka_6sem.domain.repository.AuthRepository
import com.example.semka_6sem.domain.repository.HabitRepository
import com.example.semka_6sem.domain.util.StreakCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val dao: HabitDao,
    private val authRepository: AuthRepository,
    private val firestore: FirestoreHabitDataSource,
) : HabitRepository {

    private fun todayEpoch(): Long = LocalDate.now().toEpochDay()

    override fun observeHabits(): Flow<List<HabitWithStatus>> {
        val today = todayEpoch()
        return combine(dao.observeHabits(), dao.observeChecks()) { habits, checks ->
            val checksByHabit = checks.groupBy { it.habitId }
            habits.map { entity ->
                val habit = HabitMapper.fromEntity(entity)
                val days = checksByHabit[habit.id].orEmpty().map { it.dayEpoch }.toSet()
                HabitWithStatus(
                    habit = habit,
                    doneToday = days.contains(today),
                    streak = StreakCalculator.calculate(days, today),
                )
            }
        }
    }

    override suspend fun addHabit(title: String) {
        if (title.isBlank()) return
        val habit = Habit(id = UUID.randomUUID().toString(), title = title)
        dao.insertHabit(HabitMapper.toEntity(habit))
        syncToFirestore { userId -> firestore.upsertHabit(userId, habit) }
    }

    override suspend fun deleteHabit(id: String) {
        dao.deleteHabit(id)
        syncToFirestore { userId -> firestore.deleteHabit(userId, id) }
    }

    override suspend fun toggleToday(id: String) {
        val today = todayEpoch()
        val checks = dao.observeChecks().first()
        val done = checks.any { it.habitId == id && it.dayEpoch == today }
        if (done) {
            dao.deleteCheck(id, today)
            syncToFirestore { userId -> firestore.deleteCheck(userId, id, today) }
        } else {
            dao.insertCheck(HabitCheckEntity(id, today))
            syncToFirestore { userId -> firestore.upsertCheck(userId, id, today) }
        }
    }

    override suspend fun clearAll() {
        dao.clearChecks()
        dao.clearHabits()
        val userId = authRepository.userId.first()
        if (userId != null) {
            firestore.clearAll(userId)
        }
    }

    override suspend fun seedDemoHabitsIfEmpty() {
        if (dao.habitCount() > 0) return
        listOf("Пить воду", "Зарядка", "Чтение 15 мин").forEach { title ->
            addHabit(title)
        }
    }

    override suspend fun syncFromCloud() {
        val userId = authRepository.userId.first() ?: return
        pushLocalToFirestore(userId)
        val (habits, checks) = firestore.fetchAll(userId)
        if (habits.isNotEmpty() || checks.isNotEmpty()) {
            dao.clearChecks()
            dao.clearHabits()
            habits.forEach { dao.insertHabit(HabitMapper.toEntity(it)) }
            checks.forEach { dao.insertCheck(HabitCheckEntity(it.habitId, it.dayEpoch)) }
        }
    }

    private suspend fun pushLocalToFirestore(userId: String) {
        val habits = dao.observeHabits().first()
        val checks = dao.observeChecks().first()
        habits.forEach { firestore.upsertHabit(userId, HabitMapper.fromEntity(it)) }
        checks.forEach { firestore.upsertCheck(userId, it.habitId, it.dayEpoch) }
    }

    private suspend fun syncToFirestore(block: suspend (String) -> Unit) {
        val userId = authRepository.userId.first() ?: return
        block(userId)
    }
}
