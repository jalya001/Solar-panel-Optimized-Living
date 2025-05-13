package no.solcellepanelerApp.ui.reusables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.navigation.NavController
import no.solcellepanelerApp.R
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.navigation.AppearanceBottomSheet
import no.solcellepanelerApp.ui.navigation.BottomBar
import no.solcellepanelerApp.ui.navigation.HelpBottomSheet
import no.solcellepanelerApp.ui.navigation.TopBar
import no.solcellepanelerApp.ui.onboarding.OnboardingUtils

@Composable

fun AppScaffold(
    navController: NavController,
    appScaffoldController: AppScaffoldController,
    fontScaleViewModel: FontScaleViewModel,
    showHelp: Boolean,
    onHelpChange: (Boolean) -> Unit,
    showAppearance: Boolean,
    onAppearanceChange: (Boolean) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val context = LocalContext.current

    val onboardingUtils = remember { OnboardingUtils(context) }

    var showHomeOverlay by remember { mutableStateOf(false) }
    var showMapOverlay by remember { mutableStateOf(true) }
    var showDrawOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!onboardingUtils.isHomeOverlayShown()) {
            showHomeOverlay = true
            onboardingUtils.setHomeOverlayShown()
        }
        if (!onboardingUtils.isMapOverlayShown()) {
            showMapOverlay = true
            onboardingUtils.setMapOverlayShown()
        }
        if (!onboardingUtils.isDrawOverlayShown()) {
            showDrawOverlay = true
            onboardingUtils.isDrawOverlayShown()
        }
    }

    if (showHomeOverlay) {
        val title = stringResource(R.string.home_overlay_title)
        val body = stringResource(R.string.home_overlay)
        val message = buildAnnotatedString {
            withStyle(style = MaterialTheme.typography.titleLarge.toSpanStyle()) {
                append("$title\n\n")
            }
            withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {
                append(body)
            }
        }

        SimpleTutorialOverlay(
            onDismiss = { showHomeOverlay = false },
            message = message
        )
    }

    Scaffold(
        topBar = {
            appScaffoldController.topBarContent?.invoke()
                ?: appScaffoldController.topBarTitle?.let { TopBar(navController, text = it) }
        },
        bottomBar = {
            if (appScaffoldController.isBottomBarVisible) {
                BottomBar(
                    navController = navController,
                    onHelpClicked = { onHelpChange(true) },
                    onAppearanceClicked = { onAppearanceChange(true) }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = appScaffoldController.snackbarHostState)
        }
    ) { padding ->
        content(padding)

        HelpBottomSheet(
            navController = navController,
            visible = showHelp,
            onDismiss = { onHelpChange(false) }
        )

        AppearanceBottomSheet(
            visible = showAppearance,
            onDismiss = { onAppearanceChange(false) },
            fontScaleViewModel = fontScaleViewModel
        )
    }
}
