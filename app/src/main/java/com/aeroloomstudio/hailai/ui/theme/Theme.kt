package com.aeroloomstudio.hailai.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val HailColorScheme = lightColorScheme(
    primary = HailBlue,
    onPrimary = TextOnBlue,
    primaryContainer = HailBlueLight,
    onPrimaryContainer = HailBlueDark,
    secondary = HailBlue,
    onSecondary = TextOnBlue,
    secondaryContainer = HailBlueSoft,
    onSecondaryContainer = HailBlueDark,
    tertiary = StatusGreen,
    onTertiary = Color.White,
    tertiaryContainer = StatusGreenLight,
    onTertiaryContainer = StatusGreen,
    error = StatusRed,
    onError = Color.White,
    errorContainer = StatusRedLight,
    onErrorContainer = StatusRed,
    background = SurfaceWhite,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor,
    outlineVariant = SurfaceDim,
)

@Composable
fun HailAITheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = HailColorScheme,
        typography = HailTypography,
        shapes = HailShapes,
        content = content
    )
}