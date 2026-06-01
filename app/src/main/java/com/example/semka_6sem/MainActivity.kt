package com.example.semka_6sem

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.semka_6sem.navigation.Routes
import com.example.semka_6sem.presentation.add.AddHabitScreen
import com.example.semka_6sem.presentation.add.AddHabitViewModel
import com.example.semka_6sem.presentation.list.HabitListScreen
import com.example.semka_6sem.presentation.list.HabitListViewModel
import com.example.semka_6sem.settings.SettingsActivity
import com.example.semka_6sem.ui.theme.Semka_6semTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationsIfNeeded()

        setContent {
            Semka_6semTheme {
                val navController = rememberNavController()
                val listViewModel: HabitListViewModel = hiltViewModel()
                val listState by listViewModel.uiState.collectAsStateWithLifecycle()

                NavHost(navController = navController, startDestination = Routes.LIST) {
                    composable(Routes.LIST) {
                        HabitListScreen(
                            uiState = listState,
                            onAddClick = { navController.navigate(Routes.ADD) },
                            onSettingsClick = {
                                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                            },
                            onToggle = listViewModel::toggleToday,
                            onDelete = listViewModel::deleteHabit,
                        )
                    }
                    composable(Routes.ADD) {
                        val addViewModel: AddHabitViewModel = hiltViewModel()
                        val addState by addViewModel.uiState.collectAsStateWithLifecycle()
                        AddHabitScreen(
                            uiState = addState,
                            onTitleChange = addViewModel::onTitleChange,
                            onSave = addViewModel::save,
                            onScannedText = addViewModel::applyScannedText,
                            onSaved = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }

    private fun requestNotificationsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
