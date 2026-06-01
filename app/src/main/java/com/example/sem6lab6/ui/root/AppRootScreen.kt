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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.sem6lab6.firebase.RemoteConfigService
import com.example.sem6lab6.profile.ProfileRepository
import com.example.sem6lab6.profile.ProfileScreen
import com.example.sem6lab6.profile.ProfileViewModel
import com.example.sem6lab6.profile.ProfileViewModelFactory
import com.example.sem6lab6.ui.movies.MovieScreen
import com.example.sem6lab6.ui.movies.MovieViewModel
import com.example.sem6lab6.ui.movies.MovieViewModelFactory

@Composable
fun AppRootScreen(
    loginViewModel: LoginViewModel,
    movieRepository: InMemoryMovieRepository,
    analyticsService: AnalyticsService,
    remoteConfigService: RemoteConfigService,
    profileRepository: ProfileRepository,
    targetScreen: String
) {
    val loginState by loginViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        remoteConfigService.start()
    }

    if (!loginState.isAuthenticated) {
        LoginScreen(viewModel = loginViewModel)
        return
    }

    MainScreen(
        userName = loginState.userName ?: "Гость",
        onLogout = loginViewModel::logout,
        movieRepository = movieRepository,
        analyticsService = analyticsService,
        remoteConfigService = remoteConfigService,
        profileRepository = profileRepository,
        targetScreen = targetScreen
    )
}

@Composable
private fun MainScreen(
    userName: String,
    onLogout: () -> Unit,
    movieRepository: InMemoryMovieRepository,
    analyticsService: AnalyticsService,
    remoteConfigService: RemoteConfigService,
    profileRepository: ProfileRepository,
    targetScreen: String
) {
    val remoteConfigState by remoteConfigService.state.collectAsStateWithLifecycle()
    val movieViewModel: MovieViewModel = viewModel(
        factory = MovieViewModelFactory(
            movieRepository = movieRepository,
            analyticsService = analyticsService
        )
    )
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(profileRepository)
    )
    var selectedTab by remember { mutableIntStateOf(targetScreen.toTabIndex()) }

    LaunchedEffect(userName) {
        profileViewModel.start(userName)
    }

    LaunchedEffect(targetScreen) {
        selectedTab = targetScreen.toTabIndex()
    }

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
                text = remoteConfigState.welcomeBanner,
                color = Color(0xFFE6DDF7)
            )
            Text(
                text = "Вы вошли как $userName",
                color = Color(0xFFE6DDF7)
            )
            if (remoteConfigState.experimentalProfileEnabled) {
                Text(
                    text = "Экспериментальный профиль включен через Remote Config",
                    color = Color(0xFFCDECD4)
                )
            }
            RowTabs(
                selectedTab = selectedTab,
                showProfile = remoteConfigState.experimentalProfileEnabled,
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
            2 -> ProfileScreen(userName = userName, viewModel = profileViewModel)
        }
    }
}

@Composable
private fun RowTabs(
    selectedTab: Int,
    showProfile: Boolean,
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
        if (showProfile) {
            Button(
                onClick = { onSelectTab(2) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Профиль")
            }
        }
    }
}

private fun String.toTabIndex(): Int {
    return when (lowercase()) {
        "about" -> 1
        "profile" -> 2
        else -> 0
    }
}
