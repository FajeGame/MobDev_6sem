package com.example.sem6lab6.domain.repository

import com.example.sem6lab6.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun observeMovies(): Flow<List<Movie>>

    fun observeMovie(movieId: String): Flow<Movie?>

    suspend fun addMovie(movie: Movie)

    suspend fun deleteMovie(movieId: String)

    suspend fun toggleFavorite(movieId: String)

    suspend fun toggleWatched(movieId: String)
}
