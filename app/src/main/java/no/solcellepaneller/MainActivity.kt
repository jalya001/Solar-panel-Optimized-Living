package no.solcellepaneller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import no.solcellepaneller.ui.language.LanguageUtils
import no.solcellepaneller.ui.navigation.Nav
import no.solcellepaneller.ui.font.FontScaleViewModel
import no.solcellepaneller.ui.theme.SolcellepanellerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val languageCode = LanguageUtils.getSavedLanguage(this) ?: "en"
        LanguageUtils.setLanguage(this, languageCode)
        enableEdgeToEdge()
        setContent {
            SolcellepanellerTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    val fontScaleViewModel: FontScaleViewModel = viewModel()

    val systemFontScale = LocalDensity.current.fontScale
    val effectiveFontScale = systemFontScale * fontScaleViewModel.fontScale.floatValue
    val clampedFontScale = effectiveFontScale.coerceIn(1.0f, 1.6f)

    SolcellepanellerTheme(
        fontScale = clampedFontScale
    ) {
        Nav(navController = navController, fontScaleViewModel = fontScaleViewModel)
    }
}
