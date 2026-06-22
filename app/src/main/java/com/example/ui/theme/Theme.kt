package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MomentoPrimary,
    onPrimary = Color(0xFF1000A9),
    primaryContainer = MomentoPrimaryContainer,
    onPrimaryContainer = MomentoOnPrimaryContainer,
    secondary = MomentoSecondary,
    onSecondary = Color(0xFF3C0091),
    secondaryContainer = MomentoSecondaryContainer,
    onSecondaryContainer = Color(0xFFC4ABFF),
    tertiary = MomentoTertiary,
    onTertiary = Color(0xFF003824),
    error = MomentoError,
    onError = Color(0xFF690005),
    errorContainer = MomentoErrorContainer,
    background = MomentoBackground,
    onBackground = MomentoOnSurface,
    surface = MomentoSurfaceBase,
    onSurface = MomentoOnSurface,
    surfaceVariant = MomentoSurfaceContainerHighest,
    onSurfaceVariant = MomentoOnSurfaceVariant,
    outline = MomentoOutline
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
