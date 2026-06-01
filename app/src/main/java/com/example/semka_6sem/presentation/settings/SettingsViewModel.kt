package com.example.semka_6sem.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semka_6sem.data.settings.SettingsRepository
import com.example.semka_6sem.domain.repository.AuthRepository
import com.example.semka_6sem.domain.repository.HabitRepository
import com.example.semka_6sem.domain.usecase.ClearAllDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val userId: String? = null,
    val showDeleteDialog: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val habitRepository: HabitRepository,
    private val clearAllDataUseCase: ClearAllDataUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.notificationsEnabled,
                authRepository.userId,
            ) { enabled, userId ->
                SettingsUiState(
                    notificationsEnabled = enabled,
                    userId = userId,
                )
            }.collect { _uiState.value = it.copy(showDeleteDialog = _uiState.value.showDeleteDialog) }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationsEnabled(enabled)
        }
    }

    fun onYandexLoginSuccess(userId: String) {
        viewModelScope.launch {
            authRepository.setUserId(userId)
            habitRepository.syncFromCloud()
        }
    }

    fun showDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = true)
    }

    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = false)
    }

    fun deleteAccount() {
        viewModelScope.launch {
            clearAllDataUseCase()
            hideDeleteDialog()
        }
    }
}
