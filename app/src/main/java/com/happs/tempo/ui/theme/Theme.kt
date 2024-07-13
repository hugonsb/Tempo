package com.happs.tempo.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val DarkColorScheme = darkColorScheme(
    primary = bg_top_dark,
    secondary = bg_bottom_dark,
)

private val LightColorScheme = lightColorScheme(
    primary = bg_top_light,
    secondary = bg_bottom_light,

    /* Other default colors to override
    tertiary = Pink40
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun TempoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> {
            DarkColorScheme
        }

        else -> {
            LightColorScheme
        }
    }

    (LocalView.current.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
    (LocalView.current.context as Activity).window.navigationBarColor = colorScheme.secondary.toArgb()

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}