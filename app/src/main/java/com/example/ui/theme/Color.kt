package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Components use these aliases so every screen follows the active theme.
val MomentoBackground @Composable get() = MaterialTheme.colorScheme.background
val MomentoSurfaceBase @Composable get() = MaterialTheme.colorScheme.surface
val MomentoSurfaceContainer @Composable get() = MaterialTheme.colorScheme.surfaceVariant
val MomentoSurfaceContainerHigh @Composable get() = MaterialTheme.colorScheme.surfaceVariant
val MomentoSurfaceContainerHighest @Composable get() = MaterialTheme.colorScheme.surfaceVariant
val MomentoOnSurface @Composable get() = MaterialTheme.colorScheme.onSurface
val MomentoOnSurfaceVariant @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
val MomentoPrimary @Composable get() = MaterialTheme.colorScheme.primary
val MomentoPrimaryContainer @Composable get() = MaterialTheme.colorScheme.primaryContainer
val MomentoOnPrimaryContainer @Composable get() = MaterialTheme.colorScheme.onPrimaryContainer
val MomentoSecondary @Composable get() = MaterialTheme.colorScheme.secondary
val MomentoSecondaryContainer @Composable get() = MaterialTheme.colorScheme.secondaryContainer
val MomentoTertiary @Composable get() = MaterialTheme.colorScheme.tertiary
val MomentoError @Composable get() = MaterialTheme.colorScheme.error
val MomentoErrorContainer @Composable get() = MaterialTheme.colorScheme.errorContainer
val MomentoOutline @Composable get() = MaterialTheme.colorScheme.outline

internal val Ink = Color(0xFF20212A)
internal val SecondaryInk = Color(0xFF686A76)
internal val Paper = Color(0xFFF7F7F9)
internal val White = Color(0xFFFFFFFF)
internal val Violet = Color(0xFF6857C8)
internal val PaleViolet = Color(0xFFEAE6FF)
internal val DeepViolet = Color(0xFF30266D)
internal val Sage = Color(0xFF26836C)
internal val MutedRed = Color(0xFFB54748)
internal val Hairline = Color(0xFFDCDCE4)

internal val DarkInk = Color(0xFFF2F1F6)
internal val DarkSecondaryInk = Color(0xFFB0ADBA)
internal val DarkBackground = Color(0xFF111116)
internal val DarkSurface = Color(0xFF1A1A21)
internal val DarkSurfaceVariant = Color(0xFF25242D)
internal val DarkViolet = Color(0xFFB7A9FF)
internal val DarkPaleViolet = Color(0xFF39324F)
internal val DarkSage = Color(0xFF80CBB3)
internal val DarkRed = Color(0xFFFFB4AB)
internal val DarkHairline = Color(0xFF393740)

