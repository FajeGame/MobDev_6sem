package com.example.sem6lab6.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    secondary = SoftLilac,
    tertiary = Violet,
    background = Midnight,
    surface = Indigo,
    onPrimary = Midnight,
    onSecondary = Midnight,
    onTertiary = SoftLilac,
    onBackground = SoftLilac,
    onSurface = SoftLilac
)

private val LightColorScheme = lightColorScheme(
    primary = Violet,
    secondary = Gold,
    tertiary = Indigo,
    background = Color(0xFFF4EFFA),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Midnight,
    onTertiary = Color.White,
    onBackground = Midnight,
    onSurface = Midnight
)

@Composable
fun Sem6lab6Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
