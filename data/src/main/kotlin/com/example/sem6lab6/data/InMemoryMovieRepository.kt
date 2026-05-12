package com.example.sem6lab6.data

import com.example.sem6lab6.domain.model.Movie
import com.example.sem6lab6.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID

class InMemoryMovieRepository : MovieRepository {
    private val movies = MutableStateFlow(
        listOf(
            MovieEntity(
                id = "1",
                title = "Груз 200",
                genre = "Драма",
                year = 2007,
                durationMinutes = 95,
                rating = 7.0,
                description = "Тяжелый и очень мрачный фильм про странные события в позднем СССР."
            ),
            MovieEntity(
                id = "2",
                title = "Необъятный океан",
                genre = "Комедия",
                year = 2014,
                durationMinutes = 102,
                rating = 8.4,
                description = "Абсурдная, шумная и очень студенческая история про отдых у моря."
            ),
            MovieEntity(
                id = "3",
                title = "Как Витька Чеснок вёз Лёху Штыря в дом инвалидов",
                genre = "Драма",
                year = 2017,
                durationMinutes = 90,
                rating = 7.5,
                description = "Дорога, семейные разборки и очень неловкая российская реальность."
            ),
            MovieEntity(
                id = "4",
                title = "Укрощение строптивого",
                genre = "Комедия",
                year = 1980,
                durationMinutes = 107,
                rating = 8.0,
                description = "Классическая комедия про характер, упрямство и семейный хаос."
            ),
            MovieEntity(
                id = "5",
                title = "Шрэк",
                genre = "Мультфильм",
                year = 2001,
                durationMinutes = 90,
                rating = 8.1,
                description = "Зеленый огр, болото и идеальная сказка с кривой душой."
            ),
            MovieEntity(
                id = "6",
                title = "Муви 43",
                genre = "Комедия",
                year = 2013,
                durationMinutes = 94,
                rating = 4.8,
                description = "Очень странный набор скетчей, который неловко рекомендовать всерьез."
            ),
            MovieEntity(
                id = "7",
                title = "Очень страшное кино",
                genre = "Пародия",
                year = 2000,
                durationMinutes = 88,
                rating = 6.9,
                description = "Пародия на ужастики, шутки на грани и немного совсем мимо."
            ),
            MovieEntity(
                id = "8",
                title = "Борат",
                genre = "Комедия",
                year = 2006,
                durationMinutes = 84,
                rating = 7.3,
                description = "Неловкая, провокационная и очень мемная поездка по Америке."
            )
        )
    )

    override fun observeMovies(): Flow<List<Movie>> {
        return movies.asStateFlow().map { movieEntities ->
            movieEntities.map(MovieEntity::toDomain)
        }
    }

    override fun observeMovie(movieId: String): Flow<Movie?> {
        return movies.asStateFlow().map { movieEntities ->
            movieEntities.firstOrNull { it.id == movieId }?.toDomain()
        }
    }

    override suspend fun addMovie(movie: Movie) {
        val newMovie = movie.toEntity().copy(
            id = if (movie.id.isBlank()) UUID.randomUUID().toString() else movie.id
        )
        movies.update { movieEntities ->
            movieEntities + newMovie
        }
    }

    override suspend fun deleteMovie(movieId: String) {
        movies.update { movieEntities ->
            movieEntities.filterNot { it.id == movieId }
        }
    }

    override suspend fun toggleFavorite(movieId: String) {
        movies.update { movieEntities ->
            movieEntities.map { movie ->
                if (movie.id == movieId) {
                    movie.copy(isFavorite = !movie.isFavorite)
                } else {
                    movie
                }
            }
        }
    }

    override suspend fun toggleWatched(movieId: String) {
        movies.update { movieEntities ->
            movieEntities.map { movie ->
                if (movie.id == movieId) {
                    movie.copy(isWatched = !movie.isWatched)
                } else {
                    movie
                }
            }
        }
    }
}
