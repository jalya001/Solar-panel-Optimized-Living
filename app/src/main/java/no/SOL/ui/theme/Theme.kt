package no.SOL.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity

@Composable
fun isDarkThemeEnabled(): Boolean = when (ThemeState.themeMode) {
    ThemeMode.DARK -> true
    ThemeMode.LIGHT -> false
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
}

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


enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

object ThemeState {
    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
}

@Composable
fun SOLTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontScale: Float = LocalDensity.current.fontScale,
    content: @Composable () -> Unit,
) {

    val colors = when (ThemeState.themeMode) {
        ThemeMode.DARK -> darkScheme
        ThemeMode.LIGHT -> lightScheme
        ThemeMode.SYSTEM -> if (darkTheme) darkScheme else lightScheme
    }

    val scaledTypography = Typography(
        displayLarge = myTypography.displayLarge.copy(fontSize = myTypography.displayLarge.fontSize * fontScale),
        displayMedium = myTypography.displayMedium.copy(fontSize = myTypography.displayMedium.fontSize * fontScale),
        displaySmall = myTypography.displaySmall.copy(fontSize = myTypography.displaySmall.fontSize * fontScale),

        headlineLarge = myTypography.headlineLarge.copy(fontSize = myTypography.headlineLarge.fontSize * fontScale),
        headlineMedium = myTypography.headlineMedium.copy(fontSize = myTypography.headlineMedium.fontSize * fontScale),
        headlineSmall = myTypography.headlineSmall.copy(fontSize = myTypography.headlineSmall.fontSize * fontScale),

        titleLarge = myTypography.titleLarge.copy(fontSize = myTypography.titleLarge.fontSize * fontScale),
        titleMedium = myTypography.titleMedium.copy(fontSize = myTypography.titleMedium.fontSize * fontScale),
        titleSmall = myTypography.titleSmall.copy(fontSize = myTypography.titleSmall.fontSize * fontScale),

        bodyLarge = myTypography.bodyLarge.copy(fontSize = myTypography.bodyLarge.fontSize * fontScale),
        bodyMedium = myTypography.bodyMedium.copy(fontSize = myTypography.bodyMedium.fontSize * fontScale),
        bodySmall = myTypography.bodySmall.copy(fontSize = myTypography.bodySmall.fontSize * fontScale),


        labelLarge = myTypography.labelLarge.copy(fontSize = myTypography.labelLarge.fontSize * fontScale),
        labelMedium = myTypography.labelMedium.copy(fontSize = myTypography.labelMedium.fontSize * fontScale),
        labelSmall = myTypography.labelSmall.copy(fontSize = myTypography.labelSmall.fontSize * fontScale)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = scaledTypography,
        content = content
    )
}


