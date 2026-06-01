package com.example.semka_6sem.presentation.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.semka_6sem.R
import com.example.semka_6sem.util.TextRecognitionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    uiState: AddHabitUiState,
    onTitleChange: (String) -> Unit,
    onSave: () -> Unit,
    onScannedText: (String) -> Unit,
    onSaved: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val text = withContext(Dispatchers.IO) {
                    runCatching { TextRecognitionHelper.recognize(context, uri) }.getOrDefault("")
                }
                onScannedText(text)
            }
        }
    }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onSaved()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.add_habit)) }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.habit_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            uiState.error?.let {
                Text(text = it, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { picker.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.scan_text))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onSave,
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator()
                } else {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}
