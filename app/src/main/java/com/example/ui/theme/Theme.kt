package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = DeepVibrantTeal,
    onPrimary = Color.White,
    primaryContainer = SoftTealTint,
    onPrimaryContainer = DarkPine,
    secondary = MetallicGold,
    onSecondary = DarkPine,
    secondaryContainer = GoldHighlight,
    onSecondaryContainer = DarkPine,
    tertiary = LuminousCyan,
    onTertiary = DarkPine,
    background = CanvasMint,
    onBackground = DarkPine,
    surface = SurfaceWhite,
    onSurface = DarkPine,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = SlateTealMuted,
    outline = BorderTealGray,
    outlineVariant = BorderTealLight
)

private val DarkColorScheme = darkColorScheme(
    primary = LuminousCyan,
    onPrimary = DarkPine,
    primaryContainer = Color(0xFF16382D),
    onPrimaryContainer = CanvasMint,
    secondary = MetallicGold,
    onSecondary = DarkPine,
    secondaryContainer = Color(0xFF332B18),
    onSecondaryContainer = GoldHighlight,
    tertiary = DeepVibrantTeal,
    onTertiary = Color.White,
    background = DarkPine,
    onBackground = CanvasMint,
    surface = Color(0xFF152F27),
    onSurface = CanvasMint,
    surfaceVariant = Color(0xFF1B3D33),
    onSurfaceVariant = Color(0xFFA2C7BC),
    outline = Color(0xFF264F43),
    outlineVariant = Color(0xFF1F4339)
)

@Composable
fun NoorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

