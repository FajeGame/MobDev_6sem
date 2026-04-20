package com.example.sem6lab3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sem6lab3.data.TodoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = AppGraph.repository(app)
    val todos: StateFlow<List<TodoEntity>> = repository.todos.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun add(title: String) = viewModelScope.launch {
        if (title.isNotBlank()) {
            repository.add(title)
        }
    }

    fun toggle(todo: TodoEntity) = viewModelScope.launch {
        repository.toggle(todo)
    }

    companion object {
        fun factory(app: Application) = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return TodoViewModel(app) as T
            }
        }
    }
}
