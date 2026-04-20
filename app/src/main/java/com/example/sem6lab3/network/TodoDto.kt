package com.example.sem6lab3.network

import com.example.sem6lab3.data.TodoEntity

data class TodoDto(
    val userId: Long,
    val id: Long,
    val title: String,
    val completed: Boolean
)

data class TodoRequest(
    val title: String,
    val completed: Boolean,
    val userId: Long = 1
)

fun TodoDto.toEntity() = TodoEntity(title = title, completed = completed, remoteId = id, synced = true)
