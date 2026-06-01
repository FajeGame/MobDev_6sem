package com.example.semka_6sem.data.di

import android.content.Context
import androidx.room.Room
import com.example.semka_6sem.data.local.HabitDao
import com.example.semka_6sem.data.local.HabitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HabitDatabase =
        Room.databaseBuilder(context, HabitDatabase::class.java, "habits.db").build()

    @Provides
    fun provideHabitDao(database: HabitDatabase): HabitDao = database.habitDao()
}
