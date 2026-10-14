package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.model.AppThemeMode

private val SlateDarkColorScheme = darkColorScheme(
    primary = NeonOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3B1E08),
    onPrimaryContainer = NeonAmber,
    secondary = NeonAmber,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF332107),
    onSecondaryContainer = Color(0xFFFFD59E),
    tertiary = CommonBlue,
    onTertiary = Color.Black,
    background = SlateBackground,
    onBackground = SlateTextPrimary,
    surface = SlateSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateBorder
)

private val AmoledColorScheme = darkColorScheme(
    primary = NeonOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF261204),
    onPrimaryContainer = NeonAmber,
    secondary = NeonAmber,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF241503),
    onSecondaryContainer = Color(0xFFFFD59E),
    tertiary = CommonBlue,
    onTertiary = Color.Black,
    background = AmoledBackground,
    onBackground = AmoledTextPrimary,
    surface = AmoledSurface,
    onSurface = AmoledTextPrimary,
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = AmoledTextSecondary,
    outline = AmoledBorder
)

private val LightColorScheme = lightColorScheme(
    primary = DarkOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEDD5),
    onPrimaryContainer = Color(0xFF7C2D12),
    secondary = Color(0xFFD97706),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder
)

@Composable
fun VitApGrubTheme(
    themeMode: AppThemeMode = AppThemeMode.SLATE_DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.AMOLED -> AmoledColorScheme
        AppThemeMode.SLATE_DARK -> SlateDarkColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
