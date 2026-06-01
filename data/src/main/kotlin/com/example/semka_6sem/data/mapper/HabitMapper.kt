package com.example.semka_6sem.data.mapper

import com.example.semka_6sem.data.local.entity.HabitEntity
import com.example.semka_6sem.data.remote.dto.HabitDto
import com.example.semka_6sem.domain.model.Habit

// преобразование entity и dto в domain
object HabitMapper {

    fun fromEntity(entity: HabitEntity): Habit = Habit(
        id = entity.id,
        title = entity.title,
    )

    fun toEntity(habit: Habit): HabitEntity = HabitEntity(
        id = habit.id,
        title = habit.title,
    )

    fun fromDto(dto: HabitDto): Habit = Habit(
        id = dto.id,
        title = dto.title,
    )

    fun toDto(habit: Habit): HabitDto = HabitDto(
        id = habit.id,
        title = habit.title,
    )
}
