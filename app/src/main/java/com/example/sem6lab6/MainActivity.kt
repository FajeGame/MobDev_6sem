package com.example.sem6lab6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sem6lab6.data.InMemoryMovieRepository
import com.example.sem6lab6.ui.movies.MovieScreen
import com.example.sem6lab6.ui.movies.MovieViewModel
import com.example.sem6lab6.ui.movies.MovieViewModelFactory
import com.example.sem6lab6.ui.theme.Sem6lab6Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sem6lab6Theme {
                val repository = remember { InMemoryMovieRepository() }
                val factory = remember(repository) { MovieViewModelFactory(repository) }
                val movieViewModel: MovieViewModel = viewModel(
                    factory = factory
                )
                MovieScreen(viewModel = movieViewModel)
            }
        }
    }
}
