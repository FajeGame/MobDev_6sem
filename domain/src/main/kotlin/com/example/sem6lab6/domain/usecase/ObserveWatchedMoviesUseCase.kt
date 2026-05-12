package com.example.sem6lab6.domain.usecase

import com.example.sem6lab6.domain.model.Movie
import com.example.sem6lab6.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveWatchedMoviesUseCase(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(): Flow<List<Movie>> {
        return movieRepository.observeMovies().map { movies ->
            movies.filter(Movie::isWatched)
                .sortedWith(compareByDescending<Movie> { it.rating }.thenBy { it.title })
        }
    }
}
