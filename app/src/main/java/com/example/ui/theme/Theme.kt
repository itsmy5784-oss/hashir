package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MusifyDarkColorScheme = darkColorScheme(
    primary = MusifyPrimary,
    onPrimary = MusifyOnPrimary,
    primaryContainer = MusifyPrimaryContainer,
    onPrimaryContainer = MusifyOnPrimaryContainer,
    secondary = MusifySecondary,
    secondaryContainer = MusifySecondaryContainer,
    tertiary = MusifyTertiary,
    tertiaryContainer = MusifyTertiaryContainer,
    background = MusifySurface,
    onBackground = MusifyOnSurface,
    surface = MusifySurface,
    onSurface = MusifyOnSurface,
    surfaceVariant = MusifySurfaceContainerHighest,
    onSurfaceVariant = MusifyOnSurfaceVariant,
    outline = MusifyOutline,
    outlineVariant = MusifyOutlineVariant
)

@Composable
fun MusifyTheme(
    darkTheme: Boolean = true, // Force dark mode Cupertino audio experience
    content: @Composable () -> Unit
) {
    val colorScheme = MusifyDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            var currentContext: android.content.Context? = view.context
            while (currentContext is android.content.ContextWrapper) {
                if (currentContext is Activity) break
                currentContext = currentContext.baseContext
            }
            val activity = currentContext as? Activity
            activity?.window?.let { window ->
                window.statusBarColor = MusifySurfaceContainerLowest.toArgb()
                window.navigationBarColor = MusifySurfaceContainerLowest.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
