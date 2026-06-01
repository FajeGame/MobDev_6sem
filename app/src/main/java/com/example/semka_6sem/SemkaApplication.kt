package com.example.semka_6sem

import android.app.Application
import com.example.semka_6sem.domain.repository.HabitRepository
import com.example.semka_6sem.util.FirebaseInitializer
import com.example.semka_6sem.util.ReminderScheduler
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

// точка входа hilt и стартовая инициализация
@HiltAndroidApp
class SemkaApplication : Application() {

    @Inject
    lateinit var habitRepository: HabitRepository

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var yandexAuthSdk: YandexAuthSdk
        private set

    override fun onCreate() {
        super.onCreate()
        FirebaseInitializer.init(this)
        yandexAuthSdk = YandexAuthSdk.create(YandexAuthOptions(this))
        ReminderScheduler.schedule(this)
        appScope.launch {
            if (BuildConfig.IS_DEMO) {
                habitRepository.seedDemoHabitsIfEmpty()
            }
        }
    }
}
