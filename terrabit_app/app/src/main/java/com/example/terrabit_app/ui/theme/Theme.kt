package com.example.terrabit_app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF52C98A),
    primaryContainer = Color(0xFF1B4332),
    secondary = Color(0xFF8ECDB2),
    secondaryContainer = Color(0xFF1E3A2F),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2A2A2A),
    onPrimary = Color(0xFF003822),
    onPrimaryContainer = Color(0xFF9DFFD0),
    onSecondary = Color.White,
    onBackground = Color(0xFFE8E8E8),
    onSurface = Color(0xFFE8E8E8),
    onSurfaceVariant = Color(0xFFAAAAAA),
    outline = Color(0xFF3A3A3A),
    error = Color(0xFFFF6B6B)
)

private val LightColorScheme = lightColorScheme(
    primary = MainGreen,
    primaryContainer = Color(0xFFD1F5E8),
    secondary = Color(0xFF6B8E7F),
    secondaryContainer = Color(0xFFE8F5F0),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    surfaceVariant = Color(0xFFF0F0F0),
    onPrimary = Color.White,
    onPrimaryContainer = Color(0xFF003822),
    onSecondary = Color.White,
    onBackground = Color(0xFF1A2535),
    onSurface = Color(0xFF1A2535),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFE0E0E0),
    error = Color(0xFFD32F2F)
)

@Composable
fun Terrabit_appTheme(
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