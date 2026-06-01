package com.example.sem6lab6.movies

import com.example.sem6lab6.analytics.CrashReporter
import com.example.sem6lab6.domain.model.Movie
import com.example.sem6lab6.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class CrashReportingMovieRepository(
    private val delegate: MovieRepository,
    private val crashReporter: CrashReporter
) : MovieRepository {
    override fun observeMovies(): Flow<List<Movie>> = delegate.observeMovies()

    override fun observeMovie(movieId: String): Flow<Movie?> = delegate.observeMovie(movieId)

    override suspend fun addMovie(movie: Movie) {
        runReporting("addMovie title=${movie.title}") {
            delegate.addMovie(movie)
        }
    }

    override suspend fun deleteMovie(movieId: String) {
        runReporting("deleteMovie id=$movieId") {
            delegate.deleteMovie(movieId)
        }
    }

    override suspend fun toggleFavorite(movieId: String) {
        runReporting("toggleFavorite id=$movieId") {
            delegate.toggleFavorite(movieId)
        }
    }

    override suspend fun toggleWatched(movieId: String) {
        runReporting("toggleWatched id=$movieId") {
            delegate.toggleWatched(movieId)
        }
    }

    private suspend fun runReporting(context: String, block: suspend () -> Unit) {
        try {
            block()
        } catch (error: Exception) {
            crashReporter.log(context)
            crashReporter.recordException(error)
            throw error
        }
    }
}
