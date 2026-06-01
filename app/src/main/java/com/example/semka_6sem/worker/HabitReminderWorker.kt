package com.example.semka_6sem.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.semka_6sem.data.di.DataEntryPoint
import com.example.semka_6sem.util.NotificationHelper
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

// фоновая задача напоминания через workmanager
class HabitReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            DataEntryPoint::class.java,
        )
        val enabled = entryPoint.settingsRepository().notificationsEnabled.first()
        if (enabled) {
            NotificationHelper.showReminder(applicationContext)
        }
        return Result.success()
    }
}
