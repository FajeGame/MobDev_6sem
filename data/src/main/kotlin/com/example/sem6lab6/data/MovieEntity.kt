package com.example.sem6lab6.data

internal data class MovieEntity(
    val id: String,
    val title: String,
    val genre: String,
    val year: Int,
    val durationMinutes: Int,
    val rating: Double,
    val description: String,
    val isFavorite: Boolean = false,
    val isWatched: Boolean = false
)
