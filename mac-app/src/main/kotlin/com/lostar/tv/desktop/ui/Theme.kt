package com.lostar.tv.desktop.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object Palette {
    val Background = Color(0xFF07111D)
    val Sidebar = Color(0xFF0A1626)
    val Surface = Color(0xFF0E1B2C)
    val SurfaceHigh = Color(0xFF15253A)
    val SurfaceFocus = Color(0xFF1E3350)
    val Accent = Color(0xFFF4B500)
    val OnAccent = Color(0xFF071A31)
    val Text = Color(0xFFF2F5F8)
    val TextMuted = Color(0xFFA7B5BE)
    val TextDim = Color(0xFF6B7A88)
    val Divider = Color(0xFF1E2E44)
    val Live = Color(0xFFE5484D)
}

@Composable
fun LostTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Palette.Accent,
            onPrimary = Palette.OnAccent,
            secondary = Palette.SurfaceFocus,
            onSecondary = Palette.Text,
            background = Palette.Background,
            onBackground = Palette.Text,
            surface = Palette.Surface,
            onSurface = Palette.Text,
            surfaceVariant = Palette.SurfaceHigh,
            onSurfaceVariant = Palette.TextMuted,
            outline = Palette.Divider,
        ),
        content = content,
    )
}
