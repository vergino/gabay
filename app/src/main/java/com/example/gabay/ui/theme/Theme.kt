package com.example.gabay.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = GabayBlue,
    onPrimary = Color.White,
    secondary = GabayOrange,
    background = GabayBg,
    surface = GabaySurface
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF06203F),
    secondary = Color(0xFFFFB74D),
    background = Color(0xFF10151F),
    surface = Color(0xFF1A2230)
)

@Composable
fun GabayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}