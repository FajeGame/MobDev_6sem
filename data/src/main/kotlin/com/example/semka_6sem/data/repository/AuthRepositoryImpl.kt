package com.example.semka_6sem.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.semka_6sem.data.settings.SettingsRepository
import com.example.semka_6sem.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authStore by preferencesDataStore("auth")

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AuthRepository {

    override val userId: Flow<String?> = context.authStore.data.map { prefs ->
        prefs[SettingsRepository.KEY_USER_ID]
    }

    override suspend fun setUserId(id: String?) {
        context.authStore.edit { prefs ->
            if (id == null) {
                prefs.remove(SettingsRepository.KEY_USER_ID)
            } else {
                prefs[SettingsRepository.KEY_USER_ID] = id
            }
        }
    }
}
