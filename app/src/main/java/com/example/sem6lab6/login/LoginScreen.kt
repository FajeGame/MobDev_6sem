package com.example.sem6lab6.login

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sem6lab6.auth.AuthProvider
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope

@Composable
fun LoginScreen(
    viewModel: LoginViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current as ComponentActivity
    val errorMessage = uiState.errorMessage
    val yandexLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.onYandexLoginResult(activity, result.resultCode, result.data)
    }
    val vkLauncher = rememberLauncherForActivityResult(
        contract = VK.getVKAuthActivityResultContract()
    ) { result: VKAuthenticationResult ->
        viewModel.onVkLoginResult(result)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF161616),
                        Color(0xFF241B34),
                        Color(0xFF3A2B57)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0x1FFFFFFF))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Filmi from Nikitka",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Войди через Яндекс или VK, чтобы продолжить.",
                    color = Color(0xFFE6DDF7)
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFFFA8A8)
                    )
                }
                Button(
                    onClick = {
                        viewModel.startLogin(AuthProvider.YANDEX)
                        runCatching {
                            yandexLauncher.launch(viewModel.createYandexLoginIntent(activity))
                        }.onFailure { error ->
                            viewModel.onLoginLaunchFailed(AuthProvider.YANDEX, error)
                        }
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Войти через Яндекс")
                }
                Button(
                    onClick = {
                        viewModel.startLogin(AuthProvider.VK)
                        runCatching {
                            vkLauncher.launch(emptyList<VKScope>())
                        }.onFailure { error ->
                            viewModel.onLoginLaunchFailed(AuthProvider.VK, error)
                        }
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Войти через VK")
                }
                if (uiState.isLoading) {
                    Text(
                        text = "Авторизация...",
                        color = Color(0xFFE6DDF7)
                    )
                }
            }
        }
    }
}
