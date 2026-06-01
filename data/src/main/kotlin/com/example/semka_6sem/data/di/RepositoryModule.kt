package com.example.semka_6sem.data.di

import com.example.semka_6sem.data.repository.AuthRepositoryImpl
import com.example.semka_6sem.data.repository.HabitRepositoryImpl
import com.example.semka_6sem.domain.repository.AuthRepository
import com.example.semka_6sem.domain.repository.HabitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
