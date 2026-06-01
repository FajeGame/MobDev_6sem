package com.example.semka_6sem.data.remote

import android.content.Context
import com.example.semka_6sem.data.mapper.HabitMapper
import com.example.semka_6sem.data.remote.dto.HabitCheckDto
import com.example.semka_6sem.data.remote.dto.HabitDto
import com.example.semka_6sem.domain.model.Habit
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreHabitDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val firestore: FirebaseFirestore? by lazy {
        if (FirebaseApp.getApps(context).isEmpty()) null else FirebaseFirestore.getInstance()
    }

    private fun habitsRef(userId: String) =
        firestore!!.collection("users").document(userId).collection("habits")

    private fun checksRef(userId: String) =
        firestore!!.collection("users").document(userId).collection("checks")

    // сохраняет привычку в firestore если firebase подключен
    suspend fun upsertHabit(userId: String, habit: Habit) {
        runCatching {
            val fs = firestore ?: return
            val dto = HabitMapper.toDto(habit)
            fs.collection("users").document(userId).collection("habits")
                .document(habit.id).set(dto).await()
        }
    }

    suspend fun deleteHabit(userId: String, habitId: String) {
        runCatching {
            if (firestore == null) return
            habitsRef(userId).document(habitId).delete().await()
            val checks = checksRef(userId).whereEqualTo("habitId", habitId).get().await()
            for (doc in checks.documents) {
                doc.reference.delete().await()
            }
        }
    }

    suspend fun upsertCheck(userId: String, habitId: String, dayEpoch: Long) {
        runCatching {
            if (firestore == null) return
            val id = "${habitId}_$dayEpoch"
            checksRef(userId).document(id).set(HabitCheckDto(habitId, dayEpoch)).await()
        }
    }

    suspend fun deleteCheck(userId: String, habitId: String, dayEpoch: Long) {
        runCatching {
            if (firestore == null) return
            checksRef(userId).document("${habitId}_$dayEpoch").delete().await()
        }
    }

    suspend fun fetchAll(userId: String): Pair<List<Habit>, List<HabitCheckDto>> {
        if (firestore == null) return emptyList<Habit>() to emptyList()
        return runCatching {
            val habitsSnap = habitsRef(userId).get().await()
            val checksSnap = checksRef(userId).get().await()
            val habits = habitsSnap.documents.mapNotNull { doc ->
                doc.toObject(HabitDto::class.java)?.let { HabitMapper.fromDto(it.copy(id = doc.id)) }
            }
            val checks = checksSnap.documents.mapNotNull { it.toObject(HabitCheckDto::class.java) }
            habits to checks
        }.getOrElse { emptyList<Habit>() to emptyList() }
    }

    suspend fun clearAll(userId: String) {
        runCatching {
            if (firestore == null) return
            val habits = habitsRef(userId).get().await()
            for (doc in habits.documents) {
                doc.reference.delete().await()
            }
            val checks = checksRef(userId).get().await()
            for (doc in checks.documents) {
                doc.reference.delete().await()
            }
        }
    }
}
