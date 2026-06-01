package com.example.sem6lab6.login

import androidx.activity.ComponentActivity
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sem6lab6.analytics.AnalyticsService
import com.example.sem6lab6.analytics.CrashReporter
import com.example.sem6lab6.auth.AuthProvider
import com.example.sem6lab6.auth.AuthService
import com.example.sem6lab6.auth.AuthSession
import com.example.sem6lab6.auth.AuthSessionStorage
import com.vk.api.sdk.auth.VKAuthenticationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val userName: String? = null,
    val errorMessage: String? = null
)

class LoginViewModel(
    private val authService: AuthService,
    private val authSessionStorage: AuthSessionStorage,
    private val analyticsService: AnalyticsService,
    private val crashReporter: CrashReporter
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        analyticsService.trackEvent(
            name = "screen_viewed",
            params = mapOf("screen_name" to "login")
        )
        restoreSession()
    }

    fun startLogin(provider: AuthProvider) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
    }

    fun createYandexLoginIntent(activity: ComponentActivity): Intent {
        return authService.createYandexLoginIntent(activity)
    }

    fun onYandexLoginResult(activity: ComponentActivity, resultCode: Int, data: Intent?) {
        onLoginResult(
            provider = AuthProvider.YANDEX,
            result = authService.handleYandexLoginResult(activity, resultCode, data)
        )
    }

    fun onVkLoginResult(result: VKAuthenticationResult) {
        onLoginResult(
            provider = AuthProvider.VK,
            result = authService.handleVkLoginResult(result)
        )
    }

    fun onLoginLaunchFailed(provider: AuthProvider, error: Throwable) {
        onLoginResult(provider, Result.failure(error))
    }

    fun logout() {
        authSessionStorage.clear()
        _uiState.value = LoginUiState()
    }

    private fun restoreSession() {
        try {
            val session = authSessionStorage.getSession()
            if (session != null && session.isValid()) {
                _uiState.value = LoginUiState(
                    isAuthenticated = true,
                    userName = session.userName
                )
            }
        } catch (error: Exception) {
            crashReporter.log("restoreSession failed")
            crashReporter.recordException(error)
            authSessionStorage.clear()
        }
    }

    private fun onAuthSuccess(session: AuthSession) {
        authSessionStorage.saveSession(session)
        analyticsService.trackEvent(
            name = "user_logged_in",
            params = mapOf("provider" to session.provider.id)
        )
        _uiState.value = LoginUiState(
            isLoading = false,
            isAuthenticated = true,
            userName = session.userName
        )
    }

    private fun onLoginResult(provider: AuthProvider, result: Result<AuthSession>) {
        result.onSuccess { session ->
            onAuthSuccess(session)
        }.onFailure { error ->
            crashReporter.log("login_failed_${provider.id}")
            crashReporter.recordException(error)
            analyticsService.trackError("login_failed_${provider.id}", error)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = error.message ?: "Не удалось войти"
            )
        }
    }

    private fun AuthSession.isValid(): Boolean {
        return expiresAtMillis > System.currentTimeMillis() && token.isNotBlank()
    }
}

class LoginViewModelFactory(
    private val authService: AuthService,
    private val authSessionStorage: AuthSessionStorage,
    private val analyticsService: AnalyticsService,
    private val crashReporter: CrashReporter
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return LoginViewModel(
            authService = authService,
            authSessionStorage = authSessionStorage,
            analyticsService = analyticsService,
            crashReporter = crashReporter
        ) as T
    }
}
