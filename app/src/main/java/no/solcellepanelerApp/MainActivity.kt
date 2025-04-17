package no.solcellepanelerApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.language.LanguageUtils
import no.solcellepanelerApp.ui.navigation.Nav
import no.solcellepanelerApp.ui.theme.SolcellepanelerAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val languageCode = LanguageUtils.getSavedLanguage(this) ?: "en"
        LanguageUtils.setLanguage(this, languageCode)
        enableEdgeToEdge()
        setContent {
            SolcellepanelerAppTheme {
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

    SolcellepanelerAppTheme(
        fontScale = effectiveFontScale
    ) {
        Nav(navController = navController, fontScaleViewModel = fontScaleViewModel)
    }
}
