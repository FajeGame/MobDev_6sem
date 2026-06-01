package com.example.sem6lab6.domain.usecase

import com.example.sem6lab6.domain.model.Movie
import com.example.sem6lab6.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetMovieByIdUseCase(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(movieId: String): Flow<Movie?> {
        return movieRepository.observeMovie(movieId)
    }
}
