package com.example.sem6lab6.ui.root

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sem6lab6.about.AboutScreen
import com.example.sem6lab6.login.LoginScreen
import com.example.sem6lab6.login.LoginViewModel
import com.example.sem6lab6.analytics.AnalyticsService
import com.example.sem6lab6.data.InMemoryMovieRepository
import com.example.sem6lab6.ui.movies.MovieScreen
import com.example.sem6lab6.ui.movies.MovieViewModel
import com.example.sem6lab6.ui.movies.MovieViewModelFactory

@Composable
fun AppRootScreen(
    loginViewModel: LoginViewModel,
    movieRepository: InMemoryMovieRepository,
    analyticsService: AnalyticsService
) {
    val loginState by loginViewModel.uiState.collectAsStateWithLifecycle()

    if (!loginState.isAuthenticated) {
        LoginScreen(viewModel = loginViewModel)
        return
    }

    MainScreen(
        userName = loginState.userName ?: "Гость",
        onLogout = loginViewModel::logout,
        movieRepository = movieRepository,
        analyticsService = analyticsService
    )
}

@Composable
private fun MainScreen(
    userName: String,
    onLogout: () -> Unit,
    movieRepository: InMemoryMovieRepository,
    analyticsService: AnalyticsService
) {
    val movieViewModel: MovieViewModel = viewModel(
        factory = MovieViewModelFactory(
            movieRepository = movieRepository,
            analyticsService = analyticsService
        )
    )
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Filmi from Nikitka",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Вы вошли как $userName",
                color = Color(0xFFE6DDF7)
            )
            RowTabs(
                selectedTab = selectedTab,
                onSelectTab = { selectedTab = it }
            )
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Выйти")
            }
        }

        HorizontalDivider(color = Color(0x33FFFFFF))

        when (selectedTab) {
            0 -> MovieScreen(viewModel = movieViewModel)
            1 -> AboutScreen()
        }
    }
}

@Composable
private fun RowTabs(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = { onSelectTab(0) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Фильмы")
        }
        Button(
            onClick = { onSelectTab(1) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("О нас")
        }
    }
}
