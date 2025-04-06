package no.solcellepaneller.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import no.solcellepaneller.ui.font.FontSizeState
import no.solcellepaneller.ui.theme.Typography

// Må legge til 0xFF før HExveerdi
val darkPrimary =Color(0xFF0c1618)
val  darkSecondary =Color(0xFF111e21)

val  orangeColor =Color(0xFFf3a712) //Må finne bedre navn

val lightPrimary =Color(0xFFc5e3e4)
val  lightSecondary =Color(0xFFa6d5d6)

private val DarkColorScheme = darkColorScheme(
    primary = darkSecondary,
    secondary = darkSecondary,
    tertiary = orangeColor,
    background = darkPrimary,
    onPrimary = orangeColor ,
    onSurfaceVariant = orangeColor

    //  surface = Color(0xFFFFFBFE),
    //    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color.White,
//    onSurface = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = lightSecondary,
    secondary = lightSecondary,
    tertiary = darkPrimary,
    background = lightPrimary,
    onPrimary = darkPrimary ,
    onSurfaceVariant = darkPrimary

    //  surface = Color(0xFFFFFBFE),
    //    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color.White,
//    onSurface = Color.White,
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

object ThemeState {
    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
}

@Composable
fun SolcellepanellerTheme(
    fontScale: Float = LocalDensity.current.fontScale,
    content: @Composable () -> Unit
) {
    val systemInDarkTheme = isSystemInDarkTheme()

    val isDark = when (ThemeState.themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> systemInDarkTheme
    }

    val colors = if (isDark) DarkColorScheme else LightColorScheme

    val baseTypography = Typography()

    val scaledTypography = Typography(
        bodyLarge = baseTypography.bodyLarge.copy(fontSize = baseTypography.bodyLarge.fontSize * fontScale),
        bodyMedium = baseTypography.bodyMedium.copy(fontSize = baseTypography.bodyMedium.fontSize * fontScale),
        bodySmall = baseTypography.bodySmall.copy(fontSize = baseTypography.bodySmall.fontSize * fontScale),

        titleLarge = baseTypography.titleLarge.copy(fontSize = baseTypography.titleLarge.fontSize * fontScale),
        titleMedium = baseTypography.titleMedium.copy(fontSize = baseTypography.titleMedium.fontSize * fontScale),
        titleSmall = baseTypography.titleSmall.copy(fontSize = baseTypography.titleSmall.fontSize * fontScale)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = scaledTypography,
        content = content
    )}


