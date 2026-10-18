package com.jarvis.ai.ui.theme

import android.content.Context
import android.provider.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * True when the user has turned off system animations (or the app setting
 * overrides it). Animated components read this and hold still when true.
 */
val LocalReducedMotion = compositionLocalOf { false }

/** Reads the Android "Remove animations" setting (animator duration scale = 0). */
fun isSystemReducedMotion(context: Context): Boolean =
    Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    ) == 0f

private val DarkScheme = darkColorScheme(
    primary = JarvisCyan,
    onPrimary = JarvisBlack,
    secondary = JarvisBlue,
    onSecondary = JarvisBlack,
    background = JarvisBlack,
    onBackground = JarvisText,
    surface = JarvisNavy,
    onSurface = JarvisText,
    surfaceVariant = JarvisPanel,
    onSurfaceVariant = JarvisTextDim,
    outline = JarvisCyan.copy(alpha = 0.4f),
    error = JarvisAlert,
    onError = JarvisBlack
)

private val LightScheme = lightColorScheme(
    primary = JarvisBlueDeep,
    onPrimary = Color.White,
    secondary = JarvisCyanDeep,
    background = Color(0xFFF3F8FC),
    onBackground = Color(0xFF0A1A2F),
    surface = Color.White,
    onSurface = Color(0xFF0A1A2F),
    surfaceVariant = Color(0xFFE2EEF7),
    onSurfaceVariant = Color(0xFF3B5870),
    outline = JarvisBlueDeep.copy(alpha = 0.4f),
    error = Color(0xFFB00020)
)

@Composable
fun JarvisTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    val context = LocalContext.current
    CompositionLocalProvider(
        LocalReducedMotion provides isSystemReducedMotion(context)
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkScheme else LightScheme,
            typography = JarvisTypography,
            content = content
        )
    }
}
