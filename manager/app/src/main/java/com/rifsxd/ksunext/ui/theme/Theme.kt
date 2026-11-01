package com.rifsxd.ksunext.ui.theme

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.rifsxd.ksunext.ui.component.RootXBg

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
    primary = Midnight.Accent, onPrimary = Color(0xFF0B1230),
    primaryContainer = Color(0xFF2C3556), onPrimaryContainer = Color(0xFFDCE4FF),
    secondary = Color(0xFFB4C2F2), onSecondary = Color(0xFF0B1230),
    secondaryContainer = Color(0x24FFFFFF), onSecondaryContainer = Midnight.Text,
    tertiary = Color(0xFFC3B6FF), onTertiary = Color(0xFF1B0F33),
    tertiaryContainer = Color(0xFF38285F), onTertiaryContainer = Color(0xFFEADFFF),
    background = Color.Transparent, onBackground = Midnight.Text,
    surface = Color(0xFF12151F), onSurface = Midnight.Text,
    surfaceVariant = Color(0xFF232838), onSurfaceVariant = Midnight.TextSecondary,
    surfaceContainerLowest = Color(0xFF0C0E16), surfaceContainerLow = Color(0xFF181C2A),
    surfaceContainer = Color(0xFF1C2130), surfaceContainerHigh = Color(0xFF232839),
    surfaceContainerHighest = Color(0x24FFFFFF),
    outline = Color(0xFF4A5170), outlineVariant = Color(0x24FFFFFF),
    error = Color(0xFFFF7B8A), errorContainer = Color(0xFF4A1A2C), onErrorContainer = Color(0xFFFFC2CA)
)

private val MidnightShapes = Shapes(
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(Midnight.CardRadius),
    large = RoundedCornerShape(Midnight.CardRadius),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun KernelSUTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    amoledMode: Boolean = false,
    content: @Composable () -> Unit
) {
    RootXBg.load(LocalContext.current)
    val colorScheme = RootXColorScheme.copy(primary = RootXBg.accent(), onPrimary = Color(0xFF00121C))

    SystemBarStyle(
        darkMode = true
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = MidnightShapes,
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