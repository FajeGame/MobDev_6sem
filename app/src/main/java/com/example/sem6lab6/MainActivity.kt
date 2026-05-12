package com.example.sem6lab6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sem6lab6.analytics.AppMetricaAnalyticsService
import com.example.sem6lab6.auth.DefaultAuthService
import com.example.sem6lab6.auth.EncryptedAuthSessionStorage
import com.example.sem6lab6.data.InMemoryMovieRepository
import com.example.sem6lab6.login.LoginViewModel
import com.example.sem6lab6.login.LoginViewModelFactory
import com.example.sem6lab6.ui.root.AppRootScreen
import com.example.sem6lab6.ui.theme.Sem6lab6Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sem6lab6Theme {
                val analyticsService = remember { AppMetricaAnalyticsService() }
                val authService = remember { DefaultAuthService() }
                val authStorage = remember { EncryptedAuthSessionStorage(this) }
                val movieRepository = remember { InMemoryMovieRepository() }

                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(
                        authService = authService,
                        authSessionStorage = authStorage,
                        analyticsService = analyticsService
                    )
                )

                AppRootScreen(
                    loginViewModel = loginViewModel,
                    movieRepository = movieRepository,
                    analyticsService = analyticsService
                )
            }
        }
    }
}
