package com.example.sem6lab3.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PUT
import retrofit2.http.Query

interface TodoApi {
    @POST("todos")
    suspend fun createTodo(@Body body: TodoRequest): TodoDto

    @PUT("todos/{id}")
    suspend fun updateTodo(@Path("id") id: Long, @Body body: TodoRequest): TodoDto
}
