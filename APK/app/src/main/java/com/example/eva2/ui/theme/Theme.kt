package com.example.eva2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TealAccent,
    onPrimary = TextWhite,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = TextWhite,
    background = DarkBackground,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = TextWhite,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = NavyPrimary,
    background = LightBackground,
    surface = LightSurface,
    onSurface = NavyPrimary,
    surfaceVariant = LightSurfaceVariant,
    error = ErrorRed
)

@Composable
fun EVA2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
