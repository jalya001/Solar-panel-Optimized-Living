package no.solcellepaneller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import no.solcellepaneller.ui.language.LanguageUtils
import no.solcellepaneller.ui.navigation.Nav
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
    Nav(navController)
}

