package com.example.semka_6sem.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.semka_6sem.BuildConfig
import com.example.semka_6sem.R
import com.example.semka_6sem.SemkaApplication
import com.example.semka_6sem.presentation.settings.SettingsScreen
import com.example.semka_6sem.presentation.settings.SettingsViewModel
import com.example.semka_6sem.ui.theme.Semka_6semTheme
import com.example.semka_6sem.yandex.YandexAuthHelper
import dagger.hilt.android.AndroidEntryPoint

// экран настроек на xml с compose внутри
@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var yandexAuthHelper: YandexAuthHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val app = application as SemkaApplication
        yandexAuthHelper = YandexAuthHelper(this, app.yandexAuthSdk) { userId ->
            viewModel.onYandexLoginSuccess(userId)
        }

        findViewById<ComposeView>(R.id.settings_compose).setContent {
            val state = viewModel.uiState.collectAsStateWithLifecycle().value
            Semka_6semTheme {
                SettingsScreen(
                    uiState = state,
                    flavorLabel = BuildConfig.FLAVOR_LABEL,
                    backendUrl = BuildConfig.BACKEND_URL,
                    onNotificationsChange = viewModel::setNotificationsEnabled,
                    onYandexLogin = { yandexAuthHelper.login() },
                    onDeleteAccount = viewModel::showDeleteDialog,
                    onConfirmDelete = viewModel::deleteAccount,
                    onDismissDelete = viewModel::hideDeleteDialog,
                )
            }
        }
    }
}
