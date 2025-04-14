package no.solcellepaneller.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity

// Må legge til 0xFF før HExveerdi

val orangeColor = Color(0xFFf3a712) // Må finne bedre navn

// DARK Mode
val darkPrimary = Color(0xFF0c1618)
val darkSecondary = Color(0xFF111e21)

// FROST Mode
val frostPrimary = Color(0xFFc5e3e4)
val frostSecondary = Color(0xFFa6d5d6)

// LIGHT Mode
val lightPrimary = Color.White
val lightSecondary = Color(0xFFF0F0F0)


//private val DarkColorScheme = darkColorScheme(
//    primary = darkSecondary,
//    secondary = darkSecondary,
//    tertiary = orangeColor,
//    background = darkPrimary,
//    onPrimary = orangeColor,
//    onSurfaceVariant = orangeColor
//
//    //  surface = Color(0xFFFFFBFE),
//    //    onSecondary = Color.White,
////    onTertiary = Color.White,
////    onBackground = Color.White,
////    onSurface = Color.White,
//)
//
//private val LightColorScheme = lightColorScheme(
////    primary = lightSecondary,
////    secondary = lightSecondary,
////    tertiary = darkPrimary,
////    background = lightPrimary,
////    onPrimary = darkPrimary,
////    onSurfaceVariant = darkPrimary
//
//    primary = Color(0xFFEFEFEF),
//    secondary = Color(0xFFDADADA),
//    tertiary = Color(0xFF333333),
//    background = Color(0xFFFFFFFF),
//    onPrimary = Color(0xFF000000),
//    onSurfaceVariant = Color(0xFF333333)
//
//    //  surface = Color(0xFFFFFBFE),
//    //    onSecondary = Color.White,
////    onTertiary = Color.White,
////    onBackground = Color.White,
////    onSurface = Color.White,
//)

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val FrostColorScheme = lightColorScheme(
    primary = frostSecondary,
    secondary = frostSecondary,
    tertiary = darkPrimary,
    background = frostPrimary,
    onPrimary = darkPrimary,
    onSurfaceVariant = darkPrimary

    //  surface = Color(0xFFFFFBFE),
    //    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color.White,
//    onSurface = Color.White,
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM, FROST
}

object ThemeState {
    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
}

@Composable
fun SolcellepanellerTheme(
    fontScale: Float = LocalDensity.current.fontScale,
    content: @Composable () -> Unit,
) {
    val systemInDarkTheme = isSystemInDarkTheme()

    val isDark = when (ThemeState.themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT, ThemeMode.FROST -> false
        ThemeMode.SYSTEM -> systemInDarkTheme
    }

    val colors = when (ThemeState.themeMode) {
        ThemeMode.DARK -> darkScheme
        ThemeMode.LIGHT -> lightScheme
        ThemeMode.FROST -> FrostColorScheme
        ThemeMode.SYSTEM -> if (systemInDarkTheme) darkScheme else lightScheme
    }

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
    )
}


