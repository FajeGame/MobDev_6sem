package com.example.sem6lab6.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sem6lab6.analytics.CrashReporter
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
    val isLoading: Boolean = true,
    val userId: String = "",
    val profile: UserProfile? = null,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val crashReporter: CrashReporter
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    private var listenerRegistration: ListenerRegistration? = null
    private var startedUserName: String? = null

    fun start(userName: String) {
        if (startedUserName == userName) return
        startedUserName = userName
        _uiState.value = ProfileUiState(isLoading = true)

        profileRepository.ensureProfile(
            name = userName,
            onReady = { userId ->
                profileRepository.updateFcmToken(userId)
                listenProfile(userId)
            },
            onError = { error ->
                crashReporter.log("ensureProfile failed for $userName")
                crashReporter.recordException(error)
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    errorMessage = error.message ?: "Не удалось загрузить профиль"
                )
            }
        )
    }

    private fun listenProfile(userId: String) {
        listenerRegistration?.remove()
        listenerRegistration = profileRepository.listenProfile(
            userId = userId,
            onProfile = { profile ->
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    userId = userId,
                    profile = profile
                )
            },
            onError = { error ->
                crashReporter.log("listenProfile failed for $userId")
                crashReporter.recordException(error)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Ошибка подписки на профиль"
                )
            }
        )
    }

    override fun onCleared() {
        listenerRegistration?.remove()
        super.onCleared()
    }
}

class ProfileViewModelFactory(
    private val profileRepository: ProfileRepository,
    private val crashReporter: CrashReporter
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ProfileViewModel(
            profileRepository = profileRepository,
            crashReporter = crashReporter
        ) as T
    }
}
