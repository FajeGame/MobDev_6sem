package com.example.sem6lab6.domain.usecase

import com.example.sem6lab6.domain.repository.MovieRepository

class DeleteMovieUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: String) {
        movieRepository.deleteMovie(movieId)
    }
}
