package com.example.semka_6sem.data.di

import com.example.semka_6sem.data.local.HabitDao
import com.example.semka_6sem.data.settings.SettingsRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// точка входа hilt для contentprovider и worker
@EntryPoint
@InstallIn(SingletonComponent::class)
interface DataEntryPoint {
    fun habitDao(): HabitDao
    fun settingsRepository(): SettingsRepository
}
