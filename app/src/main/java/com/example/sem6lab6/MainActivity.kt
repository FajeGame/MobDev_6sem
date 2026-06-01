package com.example.sem6lab6

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sem6lab6.analytics.AppMetricaAnalyticsService
import com.example.sem6lab6.auth.DefaultAuthService
import com.example.sem6lab6.auth.EncryptedAuthSessionStorage
import com.example.sem6lab6.data.InMemoryMovieRepository
import com.example.sem6lab6.firebase.FcmTokenStore
import com.example.sem6lab6.firebase.FirebaseRemoteConfigService
import com.example.sem6lab6.login.LoginViewModel
import com.example.sem6lab6.login.LoginViewModelFactory
import com.example.sem6lab6.profile.ProfileRepository
import com.example.sem6lab6.ui.root.AppRootScreen
import com.example.sem6lab6.ui.theme.Sem6lab6Theme

class MainActivity : ComponentActivity() {
    private var targetScreen by mutableStateOf("")

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()
        targetScreen = readTargetScreen(intent)
        setContent {
            Sem6lab6Theme {
                val analyticsService = remember { AppMetricaAnalyticsService() }
                val authService = remember { DefaultAuthService() }
                val authStorage = remember { EncryptedAuthSessionStorage(this) }
                val movieRepository = remember { InMemoryMovieRepository() }
                val tokenStore = remember { FcmTokenStore(this) }
                val remoteConfigService = remember { FirebaseRemoteConfigService() }
                val profileRepository = remember { ProfileRepository(tokenStore = tokenStore) }

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
                    analyticsService = analyticsService,
                    remoteConfigService = remoteConfigService,
                    profileRepository = profileRepository,
                    targetScreen = targetScreen
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        targetScreen = readTargetScreen(intent)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val isGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!isGranted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun readTargetScreen(intent: Intent?): String {
        return intent?.getStringExtra("screen").orEmpty()
    }
}
