package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.model.AppFontFamilyOption
import com.example.data.model.FontSizeScale
import com.example.data.model.SkinColor
import com.example.data.model.ThemeMode

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    skinColor: SkinColor = SkinColor.WINDOWS_BLUE,
    fontOption: AppFontFamilyOption = AppFontFamilyOption.SYSTEM_DEFAULT,
    fontSizeScale: FontSizeScale = FontSizeScale.DEFAULT,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val accent = Color(skinColor.accentHex)
    val lightAccent = Color(skinColor.lightAccentHex)

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = accent,
            onPrimary = Color.White,
            primaryContainer = Color(0x33000000),
            onPrimaryContainer = lightAccent,
            secondary = lightAccent,
            onSecondary = Color(0xFF0F172A),
            background = Color(0xFF0A0F1D),
            onBackground = Color.White,
            surface = Color(0xFF131D31),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF1E293B),
            onSurfaceVariant = Color(0xCCFFFFFF),
            outline = Color(0x33FFFFFF)
        )
    } else {
        lightColorScheme(
            primary = accent,
            onPrimary = Color.White,
            primaryContainer = Color(0x1A0078D4),
            onPrimaryContainer = accent,
            secondary = lightAccent,
            onSecondary = Color.White,
            background = Color(0xFFF1F5F9),
            onBackground = Color(0xFF0F172A),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFFE2E8F0),
            onSurfaceVariant = Color(0xFF334155),
            outline = Color(0x22000000)
        )
    }

    val dynamicTypography = getScaledTypography(fontOption, fontSizeScale)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = dynamicTypography,
        content = content
    )
}
