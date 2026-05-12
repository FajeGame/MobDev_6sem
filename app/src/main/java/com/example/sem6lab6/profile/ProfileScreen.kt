package com.example.sem6lab6.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileScreen(
    userName: String,
    viewModel: ProfileViewModel
) {
    LaunchedEffect(userName) {
        viewModel.start(userName)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF161616),
                        Color(0xFF243227),
                        Color(0xFF304D3D)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Профиль Firebase",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Данные читаются из Firestore в реальном времени.",
                color = Color(0xFFE2F2E7)
            )

            when {
                uiState.isLoading -> ProfileCard("Статус", "Загрузка профиля...")
                uiState.errorMessage != null -> ProfileCard("Ошибка", uiState.errorMessage.orEmpty())
                else -> {
                    val profile = uiState.profile
                    ProfileCard("User ID", uiState.userId)
                    ProfileCard("Имя", profile?.name.orEmpty())
                    ProfileCard("Email", profile?.email.orEmpty())
                    ProfileCard("FCM токен", profile?.fcmToken?.ifBlank { "Пока не получен" }.orEmpty())
                    ProfileCard("Обновлено", profile?.updatedAt?.toString().orEmpty())
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0x1FFFFFFF))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                color = Color(0xFFCFE9D6),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
