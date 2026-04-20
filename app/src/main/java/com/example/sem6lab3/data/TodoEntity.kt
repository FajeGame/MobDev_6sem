package com.example.sem6lab3.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "todos", indices = [Index(value = ["remoteId"], unique = true)])
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val completed: Boolean = false,
    val remoteId: Long? = null,
    val synced: Boolean = false
)
