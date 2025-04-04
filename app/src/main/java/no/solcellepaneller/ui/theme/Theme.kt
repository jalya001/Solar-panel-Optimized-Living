package no.solcellepaneller.ui.theme

import androidx.compose.material3.MaterialTheme
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

object ThemeState {
    var isDark by mutableStateOf(false)
}

@Composable
fun SolcellepanellerTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
//    val colors = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
    val colors = if (!ThemeState.isDark) {
        LightColorScheme
    } else {
        DarkColorScheme
    }

    val current = LocalDensity.current
    val scale = FontSizeState.fontScale.value

    CompositionLocalProvider(
        LocalDensity provides Density(density = current.density, fontScale = scale)
    ) {
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )}
}