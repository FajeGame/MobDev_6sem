package com.example.lab2sem6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import kotlinx.coroutines.delay
import com.example.lab2sem6.ui.theme.Lab2sem6Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab2sem6Theme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    var showTimer by remember { mutableStateOf(false) }
    var cachedElapsedSeconds by remember { mutableLongStateOf(0L) }

    Crossfade(targetState = showTimer, label = "screenCrossfade") { timerVisible ->
        if (timerVisible) {
            TimerScreen(
                elapsedSeconds = cachedElapsedSeconds,
                onElapsedChange = { cachedElapsedSeconds = it },
                onBack = { showTimer = false }
            )
        } else {
            HomeScreen(onOpenTimer = { showTimer = true })
        }
    }
}

@Composable
fun HomeScreen(onOpenTimer: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = onOpenTimer, contentPadding = PaddingValues(20.dp, 12.dp)) {
                Text(text = "Таймер")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    elapsedSeconds: Long,
    onElapsedChange: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var running by remember { mutableStateOf(false) }
    val latestElapsed by rememberUpdatedState(elapsedSeconds)
    val animatedBackground by animateColorAsState(
        targetValue = if (running) Color(0xFFE8F5E9) else Color(0xFFFFFFFF),
        animationSpec = tween(durationMillis = 400),
        label = "backgroundColor"
    )
    val timerScale by animateFloatAsState(
        targetValue = if (running) 1.08f else 1.0f,
        animationSpec = tween(durationMillis = 250),
        label = "timerScale"
    )

    val transition = updateTransition(targetState = running, label = "statusTransition")
    val statusColor by transition.animateColor(label = "statusColor") { isRunning ->
        if (isRunning) Color(0xFF2E7D32) else Color(0xFF9E9E9E)
    }
    val statusSize by transition.animateDp(label = "statusSize") { isRunning ->
        if (isRunning) 28.dp else 18.dp
    }
    val statusRotation by transition.animateFloat(label = "statusRotation") { isRunning ->
        if (isRunning) 360f else 0f
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    LaunchedEffect(running) {
        while (running) {
            delay(1_000)
            onElapsedChange(latestElapsed + 1)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = animatedBackground,
        topBar = {
            TopAppBar(
                title = { Text(text = "Таймер") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(statusSize)
                            .rotate(statusRotation)
                            .background(statusColor, CircleShape)
                    )
                    if (running) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
                                .alpha(pulseAlpha)
                                .background(Color(0xFF81C784), CircleShape)
                        )
                    }
                }
            }

            Text(
                text = formatTime(elapsedSeconds),
                fontSize = 36.sp,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .scale(timerScale)
            )

            Column(
                modifier = Modifier.padding(top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { running = !running }) {
                    Text(text = if (running) "Пауза" else "Старт")
                }

                AnimatedVisibility(
                    visible = !running,
                    enter = fadeIn(tween(200)) + expandVertically(tween(250)),
                    exit = fadeOut(tween(150)) + shrinkVertically(tween(200))
                ) {
                    Button(onClick = { onElapsedChange(0L) }) {
                        Text(text = "Сброс")
                    }
                }
            }
        }
    }
}

private fun formatTime(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    Lab2sem6Theme {
        HomeScreen(onOpenTimer = {})
    }
}

@Preview(showBackground = true)
@Composable
fun TimerPreview() {
    Lab2sem6Theme {
        TimerScreen(
            elapsedSeconds = 75L,
            onElapsedChange = {},
            onBack = {}
        )
    }
}
