package com.rifsxd.ksunext.ui.theme

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PRIMARY,
    secondary = PRIMARY_DARK,
    tertiary = SECONDARY_DARK
)

private val LightColorScheme = lightColorScheme(
    primary = PRIMARY,
    secondary = PRIMARY_LIGHT,
    tertiary = SECONDARY_LIGHT
)

fun Color.blend(other: Color, ratio: Float): Color {
    val inverse = 1f - ratio
    return Color(
        red = red * inverse + other.red * ratio,
        green = green * inverse + other.green * ratio,
        blue = blue * inverse + other.blue * ratio,
        alpha = alpha
    )
}

private val RootXColorScheme = darkColorScheme(
    primary = Color(0xFF5FB4C8), onPrimary = Color(0xFF00181F),
    primaryContainer = Color(0xFF123C4A), onPrimaryContainer = Color(0xFFCDEFF6),
    secondary = Color(0xFF7AA8D6), onSecondary = Color(0xFF001526),
    secondaryContainer = Color(0xFF1A3550), onSecondaryContainer = Color(0xFFD6E8FA),
    tertiary = Color(0xFF8FC8D8), onTertiary = Color(0xFF00212A),
    tertiaryContainer = Color(0xFF1C3F4C), onTertiaryContainer = Color(0xFFD2EEF6),
    background = Color.Transparent, onBackground = Color(0xFFE6F1F5),
    surface = Color(0xFF08151F), onSurface = Color(0xFFE6F1F5),
    surfaceVariant = Color(0xFF122533), onSurfaceVariant = Color(0xFF9DB8C4),
    surfaceContainerLowest = Color(0xFF061018), surfaceContainerLow = Color(0xFF0C1C28),
    surfaceContainer = Color(0xFF10222F), surfaceContainerHigh = Color(0xFF162B3A),
    surfaceContainerHighest = Color(0x2EFFFFFF),
    outline = Color(0xFF3C5666), outlineVariant = Color(0xFF22394A),
    error = Color(0xFFFF7B8A), errorContainer = Color(0xFF4A1A2C), onErrorContainer = Color(0xFFFFC2CA)
)

@Composable
fun KernelSUTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    amoledMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = RootXColorScheme

    SystemBarStyle(
        darkMode = true
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
private fun SystemBarStyle(
    darkMode: Boolean,
    statusBarScrim: Color = Color.Transparent,
    navigationBarScrim: Color = Color.Transparent,
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity

    SideEffect {
        activity.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                statusBarScrim.toArgb(),
                statusBarScrim.toArgb(),
            ) { darkMode },
            navigationBarStyle = when {
                darkMode -> SystemBarStyle.dark(
                    navigationBarScrim.toArgb()
                )

                else -> SystemBarStyle.light(
                    navigationBarScrim.toArgb(),
                    navigationBarScrim.toArgb(),
                )
            }
        )
    }
}