package com.example.semka_6sem.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.semka_6sem.data.local.entity.HabitCheckEntity
import com.example.semka_6sem.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY title")
    fun observeHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit_checks")
    fun observeChecks(): Flow<List<HabitCheckEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(entity: HabitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheck(entity: HabitCheckEntity)

    @Query("DELETE FROM habit_checks WHERE habitId = :habitId AND dayEpoch = :dayEpoch")
    suspend fun deleteCheck(habitId: String, dayEpoch: Long)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabit(id: String)

    @Query("DELETE FROM habits")
    suspend fun clearHabits()

    @Query("DELETE FROM habit_checks")
    suspend fun clearChecks()

    @Query("SELECT COUNT(*) FROM habits")
    suspend fun habitCount(): Int
}
