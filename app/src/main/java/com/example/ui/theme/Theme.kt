package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanGlow,
    onPrimary = AetherBackgroundDark,
    primaryContainer = Color(0xFF1E293B),
    onPrimaryContainer = FrostWhite,
    secondary = SolarGold,
    onSecondary = AetherBackgroundDark,
    tertiary = AuroraGreen,
    background = AetherBackgroundDark,
    onBackground = FrostWhite,
    surface = Color(0xFF111827),
    onSurface = FrostWhite,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = SlateMuted
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    secondary = SolarAmber,
    tertiary = AuroraGreen,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF64748B)
)

@Composable
fun AetherWeatherTheme(
    darkTheme: Boolean = true, // Default to sleek luxury dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

