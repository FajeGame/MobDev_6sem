package com.example.sem6lab3

import android.content.Context
import androidx.room.Room
import com.example.sem6lab3.data.TodoDatabase
import com.example.sem6lab3.data.TodoRepository
import com.example.sem6lab3.network.TodoApi
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppGraph {
    private var database: TodoDatabase? = null
    private var api: TodoApi? = null

    fun repository(context: Context) = TodoRepository(databaseInstance(context), api())
    fun database(context: Context) = databaseInstance(context)

    private fun databaseInstance(context: Context) =
        database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                TodoDatabase::class.java,
                "todo.db"
            ).fallbackToDestructiveMigration().build().also { database = it }
        }

    private fun api() =
        api ?: synchronized(this) {
            api ?: Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .build()
                .create(TodoApi::class.java)
                .also { api = it }
        }
}
