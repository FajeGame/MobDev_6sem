package com.example.semka_6sem.data.di

import com.example.semka_6sem.domain.repository.AuthRepository
import com.example.semka_6sem.domain.repository.HabitRepository
import com.example.semka_6sem.domain.usecase.AddHabitUseCase
import com.example.semka_6sem.domain.usecase.ClearAllDataUseCase
import com.example.semka_6sem.domain.usecase.DeleteHabitUseCase
import com.example.semka_6sem.domain.usecase.GetHabitsUseCase
import com.example.semka_6sem.domain.usecase.ToggleTodayUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetHabits(repository: HabitRepository) = GetHabitsUseCase(repository)

    @Provides
    @Singleton
    fun provideAddHabit(repository: HabitRepository) = AddHabitUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteHabit(repository: HabitRepository) = DeleteHabitUseCase(repository)

    @Provides
    @Singleton
    fun provideToggleToday(repository: HabitRepository) = ToggleTodayUseCase(repository)

    @Provides
    @Singleton
    fun provideClearAll(
        habitRepository: HabitRepository,
        authRepository: AuthRepository,
    ) = ClearAllDataUseCase(habitRepository, authRepository)
}
