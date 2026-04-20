package com.example.sem6lab3.data

import com.example.sem6lab3.network.TodoRequest
import com.example.sem6lab3.network.TodoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TodoRepository(
    private val database: TodoDatabase,
    private val api: TodoApi
) {
    private val dao = database.todoDao()
    val todos: Flow<List<TodoEntity>> = dao.observeTodos()

    suspend fun add(title: String) = withContext(Dispatchers.IO) {
        dao.insert(TodoEntity(title = title, synced = false))
    }

    suspend fun toggle(todo: TodoEntity) = withContext(Dispatchers.IO) {
        dao.update(todo.copy(completed = !todo.completed, synced = false))
    }

    suspend fun sync() = withContext(Dispatchers.IO) {
        dao.unsyncedTodos().forEach { todo ->
            val request = TodoRequest(title = todo.title, completed = todo.completed)
            val remote = if (todo.remoteId == null) {
                api.createTodo(request)
            } else {
                api.updateTodo(todo.remoteId, request)
            }
            dao.update(todo.copy(remoteId = remote.id, synced = true))
        }
    }
}
