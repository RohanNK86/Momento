package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeModeStore {
    private val mutableDark = MutableStateFlow<Boolean?>(null)
    val dark = mutableDark.asStateFlow()

    fun setDark(context: android.content.Context, isDark: Boolean) {
        context.getSharedPreferences("momento_preferences", android.content.Context.MODE_PRIVATE)
            .edit().putBoolean("dark_theme", isDark).apply()
        mutableDark.value = isDark
    }
}

private val LightColorScheme = lightColorScheme(
    primary = Violet,
    onPrimary = Color.White,
    primaryContainer = PaleViolet,
    onPrimaryContainer = DeepViolet,
    secondary = Color(0xFF776F9F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEAE8F2),
    onSecondaryContainer = Color(0xFF302B48),
    tertiary = Sage,
    onTertiary = Color.White,
    error = MutedRed,
    onError = Color.White,
    errorContainer = Color(0xFFF7E3E2),
    background = Paper,
    onBackground = Ink,
    surface = White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFEFEFF4),
    onSurfaceVariant = SecondaryInk,
    outline = Hairline
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkViolet,
    onPrimary = Color(0xFF30266D),
    primaryContainer = DarkPaleViolet,
    onPrimaryContainer = Color(0xFFE9E1FF),
    secondary = Color(0xFFC7BEDF),
    onSecondary = Color(0xFF302B48),
    secondaryContainer = Color(0xFF403A54),
    onSecondaryContainer = Color(0xFFE6DFF6),
    tertiary = DarkSage,
    onTertiary = Color(0xFF00382B),
    error = DarkRed,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    background = DarkBackground,
    onBackground = DarkInk,
    surface = DarkSurface,
    onSurface = DarkInk,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkSecondaryInk,
    outline = DarkHairline
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val storedTheme by ThemeModeStore.dark.collectAsState()
    val darkTheme = storedTheme ?: context.getSharedPreferences("momento_preferences", android.content.Context.MODE_PRIVATE).getBoolean("dark_theme", false)
    val targetScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val primary = animateColorAsState(targetScheme.primary, tween(260), label = "theme-primary").value
    val onPrimary = animateColorAsState(targetScheme.onPrimary, tween(260), label = "theme-on-primary").value
    val primaryContainer = animateColorAsState(targetScheme.primaryContainer, tween(260), label = "theme-primary-container").value
    val onPrimaryContainer = animateColorAsState(targetScheme.onPrimaryContainer, tween(260), label = "theme-on-primary-container").value
    val secondary = animateColorAsState(targetScheme.secondary, tween(260), label = "theme-secondary").value
    val secondaryContainer = animateColorAsState(targetScheme.secondaryContainer, tween(260), label = "theme-secondary-container").value
    val tertiary = animateColorAsState(targetScheme.tertiary, tween(260), label = "theme-tertiary").value
    val error = animateColorAsState(targetScheme.error, tween(260), label = "theme-error").value
    val errorContainer = animateColorAsState(targetScheme.errorContainer, tween(260), label = "theme-error-container").value
    val background = animateColorAsState(targetScheme.background, tween(260), label = "theme-background").value
    val onBackground = animateColorAsState(targetScheme.onBackground, tween(260), label = "theme-on-background").value
    val surface = animateColorAsState(targetScheme.surface, tween(260), label = "theme-surface").value
    val onSurface = animateColorAsState(targetScheme.onSurface, tween(260), label = "theme-on-surface").value
    val surfaceVariant = animateColorAsState(targetScheme.surfaceVariant, tween(260), label = "theme-surface-variant").value
    val onSurfaceVariant = animateColorAsState(targetScheme.onSurfaceVariant, tween(260), label = "theme-on-surface-variant").value
    val outline = animateColorAsState(targetScheme.outline, tween(260), label = "theme-outline").value
    val colorScheme = targetScheme.copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        secondaryContainer = secondaryContainer,
        tertiary = tertiary,
        error = error,
        errorContainer = errorContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        outline = outline
    )
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

