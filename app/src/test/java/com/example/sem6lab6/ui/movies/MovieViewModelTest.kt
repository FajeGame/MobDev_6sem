package com.example.sem6lab6.ui.movies

import com.example.sem6lab6.MainDispatcherRule
import com.example.sem6lab6.analytics.FakeAnalyticsService
import com.example.sem6lab6.data.InMemoryMovieRepository
import com.example.sem6lab6.domain.usecase.AddMovieUseCase
import com.example.sem6lab6.domain.usecase.DeleteMovieUseCase
import com.example.sem6lab6.domain.usecase.ObserveMoviesUseCase
import com.example.sem6lab6.domain.usecase.ObserveWatchedMoviesUseCase
import com.example.sem6lab6.domain.usecase.ToggleFavoriteUseCase
import com.example.sem6lab6.domain.usecase.ToggleWatchedUseCase
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MovieViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `tracks screen viewed on init`() = runTest {
        val analytics = FakeAnalyticsService()
        createViewModel(analytics)

        assertTrue(
            analytics.events.any { event ->
                event.name == "screen_viewed" && event.params["screen_name"] == "movies"
            }
        )
    }

    @Test
    fun `tracks movie deleted when movie is deleted`() = runTest {
        val analytics = FakeAnalyticsService()
        val viewModel = createViewModel(analytics)

        viewModel.deleteMovie("1")
        advanceUntilIdle()

        assertTrue(
            analytics.events.any { event ->
                event.name == "movie_deleted" && event.params["movie_id"] == "1"
            }
        )
    }

    private fun createViewModel(analytics: FakeAnalyticsService): MovieViewModel {
        val repository = InMemoryMovieRepository()
        return MovieViewModel(
            observeMoviesUseCase = ObserveMoviesUseCase(repository),
            observeWatchedMoviesUseCase = ObserveWatchedMoviesUseCase(repository),
            addMovieUseCase = AddMovieUseCase(repository),
            deleteMovieUseCase = DeleteMovieUseCase(repository),
            toggleFavoriteUseCase = ToggleFavoriteUseCase(repository),
            toggleWatchedUseCase = ToggleWatchedUseCase(repository),
            analyticsService = analytics
        )
    }
}
