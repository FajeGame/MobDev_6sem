package com.example.semka_6sem.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.semka_6sem.R

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    flavorLabel: String,
    backendUrl: String,
    onNotificationsChange: (Boolean) -> Unit,
    onYandexLogin: () -> Unit,
    onDeleteAccount: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = stringResource(R.string.build_info, flavorLabel, backendUrl),
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = stringResource(R.string.reminder_time))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(R.string.notifications))
        Switch(
            checked = uiState.notificationsEnabled,
            onCheckedChange = onNotificationsChange,
        )
        Spacer(modifier = Modifier.height(16.dp))
        val accountText = if (uiState.userId != null) {
            stringResource(R.string.logged_in_as, uiState.userId.take(8))
        } else {
            stringResource(R.string.not_logged_in)
        }
        Text(text = accountText)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onYandexLogin, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.yandex_login))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onDeleteAccount, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.delete_account))
        }
    }

    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = onDismissDelete,
            title = { Text(stringResource(R.string.delete_account)) },
            text = { Text(stringResource(R.string.delete_account_confirm)) },
            confirmButton = {
                Button(onClick = onConfirmDelete) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                Button(onClick = onDismissDelete) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}
