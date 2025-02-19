package com.pgmv.bandify.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    secondary = Gold80,
    tertiary = Blue90,
    background = Grey80,
    surface = Grey80,
    onPrimary = White,
    onSecondary = Grey80,
    onSurface = White,
    onBackground = White,
    onTertiary = Blue80,
    error=Red,
)

private val LightColorScheme = lightColorScheme(
    primary = Blue80,
    secondary = Gold80,
    tertiary = Blue90,
    background = Grey10,
    surface = Grey10,
    onPrimary = White,
    onSecondary = Grey80,
    onSurface = Blue80,
    onBackground = Blue80,
    onTertiary = Grey20
)

@Composable
fun BandifyTheme(
    darkTheme: Boolean = false, // Força o tema claro como padrão
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}