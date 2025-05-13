package no.SOL

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import no.SOL.ui.font.FontScaleViewModel
import no.SOL.ui.language.LanguageUtils
import no.SOL.ui.navigation.AppScaffold
import no.SOL.ui.navigation.AppScaffoldController
import no.SOL.ui.navigation.Nav
import no.SOL.ui.onboarding.OnboardingScreen
import no.SOL.ui.onboarding.OnboardingUtils
import no.SOL.ui.theme.SOLTheme


class MainActivity : ComponentActivity() {

    private val onboardingUtils by lazy { OnboardingUtils(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val languageCode = LanguageUtils.getSavedLanguage(this) ?: "en"
        LanguageUtils.setLanguage(this, languageCode)
        installSplashScreen()
//        onboardingUtils.resetAllOnboardingStates() //for testing
        enableEdgeToEdge()
        setContent {
            SOLTheme {
                if (onboardingUtils.isOnboardingCompleted()) {
                    App()
                } else {
                    ShowOnboardingScreen()
                }
            }
        }
    }


    @Composable
    fun App() {
        val navController = rememberNavController()
        val fontScaleViewModel: FontScaleViewModel = viewModel()

        val context = LocalContext.current
        val appScaffoldController = remember { AppScaffoldController(context) }

        val systemFontScale = LocalDensity.current.fontScale
        val effectiveFontScale = systemFontScale * fontScaleViewModel.fontScale.floatValue

        SOLTheme(
            fontScale = effectiveFontScale
        ) {
            AppScaffold(
                navController = navController,
                controller = appScaffoldController,
                fontScaleViewModel = fontScaleViewModel,
            ) { padding ->
                Nav(
                    navController = navController,
                    appScaffoldController = appScaffoldController,
                    contentPadding = padding
                )
            }
        }
    }

    @Composable
    fun ShowOnboardingScreen() {
        val scope = rememberCoroutineScope()
        OnboardingScreen {
            onboardingUtils.setOnboardingCompleted()
            scope.launch {
                setContent {
                    App()
                }
            }
        }
    }
}