package com.example.rythmcloud.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RythmCloudLightColors = lightColorScheme(
    primary = Color(0xFF1565C0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E8FF),
    onPrimaryContainer = Color(0xFF001C38),
    secondary = Color(0xFF42A5F5),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1E9FF),
    onSecondaryContainer = Color(0xFF001D35),
    background = Color.White,
    onBackground = Color(0xFF17202A),
    surface = Color.White,
    onSurface = Color(0xFF17202A),
    surfaceVariant = Color(0xFFEAF4FF),
    onSurfaceVariant = Color(0xFF405466),
    outline = Color(0xFF70869A)
)

@Composable
fun RythmCloudTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RythmCloudLightColors,
        content = content
    )
}
