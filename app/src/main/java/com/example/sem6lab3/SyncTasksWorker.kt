package com.example.sem6lab3

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SyncTasksWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = runCatching {
        AppGraph.repository(applicationContext).sync()
        SyncPrefs.saveLastSync(applicationContext, System.currentTimeMillis())
        TodoNotifications.showSyncComplete(applicationContext)
        Result.success()
    }.getOrElse {
        Result.retry()
    }
}
