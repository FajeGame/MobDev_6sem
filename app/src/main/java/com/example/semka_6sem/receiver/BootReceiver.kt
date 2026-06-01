package com.example.semka_6sem.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.semka_6sem.util.ReminderScheduler

// пересоздаёт напоминание после перезагрузки телефона
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            ReminderScheduler.schedule(context)
        }
    }
}
