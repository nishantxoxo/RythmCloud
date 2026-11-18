package com.example.rythmcloud.ui

import com.example.rythmcloud.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

private val Poppins = FontFamily(
    Font(R.font.poppins_thin, FontWeight.Thin),
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_black, FontWeight.Black)
)

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

private val RythmCloudTypography = Typography().let { typography ->
    typography.copy(
        displayLarge = typography.displayLarge.copy(fontFamily = Poppins),
        displayMedium = typography.displayMedium.copy(fontFamily = Poppins),
        displaySmall = typography.displaySmall.copy(fontFamily = Poppins),
        headlineLarge = typography.headlineLarge.copy(fontFamily = Poppins),
        headlineMedium = typography.headlineMedium.copy(fontFamily = Poppins),
        headlineSmall = typography.headlineSmall.copy(fontFamily = Poppins),
        titleLarge = typography.titleLarge.copy(fontFamily = Poppins),
        titleMedium = typography.titleMedium.copy(fontFamily = Poppins),
        titleSmall = typography.titleSmall.copy(fontFamily = Poppins),
        bodyLarge = typography.bodyLarge.copy(fontFamily = Poppins),
        bodyMedium = typography.bodyMedium.copy(fontFamily = Poppins),
        bodySmall = typography.bodySmall.copy(fontFamily = Poppins),
        labelLarge = typography.labelLarge.copy(fontFamily = Poppins),
        labelMedium = typography.labelMedium.copy(fontFamily = Poppins),
        labelSmall = typography.labelSmall.copy(fontFamily = Poppins)
    )
}

@Composable
fun RythmCloudTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RythmCloudLightColors,
        typography = RythmCloudTypography,
        content = content
    )
}
