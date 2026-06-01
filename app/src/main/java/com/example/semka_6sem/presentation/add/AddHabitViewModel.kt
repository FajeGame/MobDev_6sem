package com.example.semka_6sem.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semka_6sem.domain.usecase.AddHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddHabitUiState(
    val title: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
)

@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val addHabitUseCase: AddHabitUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddHabitUiState())
    val uiState: StateFlow<AddHabitUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(title = value, error = null)
    }

    fun save() {
        val title = _uiState.value.title.trim()
        if (title.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Введите название")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            runCatching { addHabitUseCase(title) }
                .onSuccess {
                    _uiState.value = AddHabitUiState(saved = true)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = e.message ?: "Ошибка",
                    )
                }
        }
    }

    fun applyScannedText(text: String) {
        val line = text.lineSequence().firstOrNull { it.isNotBlank() }?.trim().orEmpty()
        if (line.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(title = line)
        }
    }
}
