package com.example.sem6lab6.domain.usecase

import com.example.sem6lab6.domain.model.Movie
import com.example.sem6lab6.domain.repository.MovieRepository

class AddMovieUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movie: Movie) {
        movieRepository.addMovie(movie)
    }
}
