package com.interviewmirror.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1D6B60),
    secondary = Color(0xFF725CFF),
    tertiary = Color(0xFFE08A1E),
    background = Color(0xFFF8FAF9),
    surface = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6CD6C4),
    secondary = Color(0xFFB8AEFF),
    tertiary = Color(0xFFFFC16D),
    background = Color(0xFF111413),
    surface = Color(0xFF1A1E1D)
)

@Composable
fun InterviewMirrorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
