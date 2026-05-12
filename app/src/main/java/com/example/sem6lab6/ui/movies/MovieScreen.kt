package com.example.sem6lab6.ui.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.sem6lab6.analytics.AnalyticsService
import com.example.sem6lab6.core.toMovieDuration
import com.example.sem6lab6.core.toRatingText
import com.example.sem6lab6.domain.model.Movie
import com.example.sem6lab6.domain.repository.MovieRepository
import com.example.sem6lab6.domain.usecase.AddMovieUseCase
import com.example.sem6lab6.domain.usecase.DeleteMovieUseCase
import com.example.sem6lab6.domain.usecase.ObserveMoviesUseCase
import com.example.sem6lab6.domain.usecase.ObserveWatchedMoviesUseCase
import com.example.sem6lab6.domain.usecase.ToggleFavoriteUseCase
import com.example.sem6lab6.domain.usecase.ToggleWatchedUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class MovieUiState(
    val allMovies: List<Movie> = emptyList(),
    val watchedMovies: List<Movie> = emptyList()
) {
    val unwatchedMovies: List<Movie>
        get() = allMovies.filterNot(Movie::isWatched)

    val watchedCount: Int get() = watchedMovies.size
    val unwatchedCount: Int get() = unwatchedMovies.size
    val favoriteCount: Int get() = allMovies.count(Movie::isFavorite)
}

class MovieViewModel(
    observeMoviesUseCase: ObserveMoviesUseCase,
    observeWatchedMoviesUseCase: ObserveWatchedMoviesUseCase,
    private val addMovieUseCase: AddMovieUseCase,
    private val deleteMovieUseCase: DeleteMovieUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val toggleWatchedUseCase: ToggleWatchedUseCase,
    private val analyticsService: AnalyticsService
) : ViewModel() {
    init {
        analyticsService.trackEvent(
            name = "screen_viewed",
            params = mapOf("screen_name" to "movies")
        )
    }

    val uiState = combine(
        observeMoviesUseCase(),
        observeWatchedMoviesUseCase()
    ) { allMovies, watchedMovies ->
        MovieUiState(
            allMovies = allMovies,
            watchedMovies = watchedMovies
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MovieUiState()
    )

    fun addMovie(
        title: String,
        genre: String,
        year: String,
        durationMinutes: String,
        rating: String,
        description: String
    ) {
        val parsedYear = year.toIntOrNull() ?: return
        val parsedDuration = durationMinutes.toIntOrNull() ?: return
        val parsedRating = rating.replace(',', '.').toDoubleOrNull() ?: return

        if (title.isBlank() || genre.isBlank() || description.isBlank()) return

        viewModelScope.launch {
            addMovieUseCase(
                Movie(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    genre = genre.trim(),
                    year = parsedYear,
                    durationMinutes = parsedDuration,
                    rating = parsedRating,
                    description = description.trim()
                )
            )
            analyticsService.trackEvent(
                name = "movie_added",
                params = mapOf("movie_title" to title.trim())
            )
        }
    }

    fun deleteMovie(movieId: String) {
        viewModelScope.launch {
            deleteMovieUseCase(movieId)
            analyticsService.trackEvent(
                name = "movie_deleted",
                params = mapOf("movie_id" to movieId)
            )
        }
    }

    fun onFavoriteClicked(movieId: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(movieId)
            analyticsService.trackEvent(
                name = "movie_favorited",
                params = mapOf("movie_id" to movieId)
            )
        }
    }

    fun onWatchedClicked(movieId: String) {
        viewModelScope.launch {
            toggleWatchedUseCase(movieId)
            analyticsService.trackEvent(
                name = "movie_watched_toggled",
                params = mapOf("movie_id" to movieId)
            )
        }
    }
}

class MovieViewModelFactory(
    private val movieRepository: MovieRepository,
    private val analyticsService: AnalyticsService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val observeMoviesUseCase = ObserveMoviesUseCase(movieRepository)
        val observeWatchedMoviesUseCase = ObserveWatchedMoviesUseCase(movieRepository)
        val addMovieUseCase = AddMovieUseCase(movieRepository)
        val deleteMovieUseCase = DeleteMovieUseCase(movieRepository)
        val toggleFavoriteUseCase = ToggleFavoriteUseCase(movieRepository)
        val toggleWatchedUseCase = ToggleWatchedUseCase(movieRepository)
        @Suppress("UNCHECKED_CAST")
        return MovieViewModel(
            observeMoviesUseCase = observeMoviesUseCase,
            observeWatchedMoviesUseCase = observeWatchedMoviesUseCase,
            addMovieUseCase = addMovieUseCase,
            deleteMovieUseCase = deleteMovieUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            toggleWatchedUseCase = toggleWatchedUseCase,
            analyticsService = analyticsService
        ) as T
    }
}

@Composable
fun MovieScreen(
    viewModel: MovieViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF161616),
                            Color(0xFF241B34),
                            Color(0xFF3A2B57)
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Фильмы на вечер",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = "Программку написал Слесарев Никита ФИТ-231",
                                color = Color(0xFFE6DDF7)
                            )
                        }
                        Button(onClick = { showAddDialog = true }) {
                            Text("Добавить фильм")
                        }
                    }
                }

                item {
                    SummaryBlock(
                        unwatchedCount = uiState.unwatchedCount,
                        watchedCount = uiState.watchedCount,
                        favoriteCount = uiState.favoriteCount
                    )
                }

                item {
                    SectionTitle(
                        title = "К просмотру",
                        subtitle = "${uiState.unwatchedCount} шт."
                    )
                }

                if (uiState.unwatchedMovies.isEmpty()) {
                    item {
                        EmptyState(text = "Здесь пока пусто. Уже всё просмотрено.")
                    }
                } else {
                    items(uiState.unwatchedMovies, key = { it.id }) { movie ->
                        MovieCard(
                            movie = movie,
                            primaryActionText = "Просмотрено",
                            secondaryActionText = if (movie.isFavorite) "Убрать из избранного" else "В избранное",
                            deleteActionText = "Удалить",
                            onPrimaryAction = { viewModel.onWatchedClicked(movie.id) },
                            onSecondaryAction = { viewModel.onFavoriteClicked(movie.id) },
                            onDeleteAction = { viewModel.deleteMovie(movie.id) }
                        )
                    }
                }

                item {
                    SectionTitle(
                        title = "Просмотрено",
                        subtitle = "${uiState.watchedCount} шт."
                    )
                }

                if (uiState.watchedMovies.isEmpty()) {
                    item {
                        EmptyState(text = "Пока ничего не отмечено.")
                    }
                } else {
                    items(uiState.watchedMovies, key = { it.id }) { movie ->
                        MovieCard(
                            movie = movie,
                            primaryActionText = "Не просмотрено",
                            secondaryActionText = if (movie.isFavorite) "Убрать из избранного" else "В избранное",
                            deleteActionText = "Удалить",
                            onPrimaryAction = { viewModel.onWatchedClicked(movie.id) },
                            onSecondaryAction = { viewModel.onFavoriteClicked(movie.id) },
                            onDeleteAction = { viewModel.deleteMovie(movie.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddMovieDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, genre, year, duration, rating, description ->
                viewModel.addMovie(title, genre, year, duration, rating, description)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddMovieDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var genre by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var duration by rememberSaveable { mutableStateOf("") }
    var rating by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить фильм") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Жанр") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Год") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Длительность в минутах") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = rating,
                    onValueChange = { rating = it },
                    label = { Text("Рейтинг") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(title, genre, year, duration, rating, description)
                }
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun SummaryBlock(
    unwatchedCount: Int,
    watchedCount: Int,
    favoriteCount: Int
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x1AFFFFFF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryChip(title = "К просмотру", value = unwatchedCount.toString())
            SummaryChip(title = "Просмотрено", value = watchedCount.toString())
            SummaryChip(title = "Избранное", value = favoriteCount.toString())
        }
    }
}

@Composable
private fun SummaryChip(
    title: String,
    value: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x24FFFFFF))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                color = Color(0xFFE0D5F7),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            color = Color(0xFFD7C8F0)
        )
    }
}

@Composable
private fun EmptyState(text: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x18FFFFFF))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            color = Color(0xFFE8E0F4)
        )
    }
}

@Composable
private fun MovieCard(
    movie: Movie,
    primaryActionText: String,
    secondaryActionText: String,
    deleteActionText: String,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
    onDeleteAction: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0x1FFFFFFF))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = movie.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${movie.genre} • ${movie.year} • ${movie.durationMinutes.toMovieDuration()}",
                    color = Color(0xFFD8C8F7)
                )
                Text(
                    text = "Рейтинг: ${movie.rating.toRatingText()}",
                    color = Color(0xFFFFD166),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = movie.description,
                    color = Color(0xFFF4EDFF)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPrimaryAction,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(primaryActionText)
                }
                Button(
                    onClick = onSecondaryAction,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(secondaryActionText)
                }
                Button(
                    onClick = onDeleteAction,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(deleteActionText)
                }
            }
        }
    }
}
