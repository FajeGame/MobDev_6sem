package com.example.semka_6sem.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semka_6sem.domain.model.HabitWithStatus
import com.example.semka_6sem.domain.usecase.DeleteHabitUseCase
import com.example.semka_6sem.domain.usecase.GetHabitsUseCase
import com.example.semka_6sem.domain.usecase.ToggleTodayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HabitListUiState {
    data object Loading : HabitListUiState
    data class Success(val habits: List<HabitWithStatus>) : HabitListUiState
    data class Error(val message: String) : HabitListUiState
}

@HiltViewModel
class HabitListViewModel @Inject constructor(
    getHabitsUseCase: GetHabitsUseCase,
    private val toggleTodayUseCase: ToggleTodayUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HabitListUiState>(HabitListUiState.Loading)
    val uiState: StateFlow<HabitListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getHabitsUseCase()
                .catch { e ->
                    _uiState.value = HabitListUiState.Error(e.message ?: "error")
                }
                .collect { habits ->
                    _uiState.value = HabitListUiState.Success(habits)
                }
        }
    }

    fun toggleToday(id: String) {
        viewModelScope.launch {
            runCatching { toggleTodayUseCase(id) }
        }
    }

    fun deleteHabit(id: String) {
        viewModelScope.launch {
            deleteHabitUseCase(id)
        }
    }
}
