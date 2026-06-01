package com.example.semka_6sem.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.semka_6sem.util.HabitShareHelper
import androidx.compose.ui.unit.dp
import com.example.semka_6sem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(
    uiState: HabitListUiState,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onToggle: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState is HabitListUiState.Error) {
            snackbar.showSnackbar(uiState.message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.habits_title)) },
                actions = {
                    IconButton(onClick = { HabitShareHelper.share(context) }) {
                        Icon(Icons.Default.Share, contentDescription = stringResource(R.string.share_habits))
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_habit))
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Crossfade(targetState = uiState, label = "listState") { state ->
                when (state) {
                    HabitListUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is HabitListUiState.Error -> {
                        Text(
                            text = stringResource(R.string.error_load),
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                    is HabitListUiState.Success -> {
                        if (state.habits.isEmpty()) {
                            Text(
                                text = stringResource(R.string.empty_habits),
                                modifier = Modifier.align(Alignment.Center),
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items(state.habits, key = { it.habit.id }) { item ->
                                    AnimatedVisibility(
                                        visible = true,
                                        enter = fadeIn() + slideInVertically(),
                                        exit = fadeOut(),
                                    ) {
                                        HabitCard(
                                            item = item,
                                            onToggle = { onToggle(item.habit.id) },
                                            onDelete = { onDelete(item.habit.id) },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
