package no.solcellepanelerApp.ui.reusables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.navigation.AppearanceBottomSheet
import no.solcellepanelerApp.ui.navigation.BottomBar
import no.solcellepanelerApp.ui.navigation.HelpBottomSheet
import no.solcellepanelerApp.ui.navigation.TopBar
@Composable

fun AppScaffold(
    navController: NavController,
    appScaffoldController: AppScaffoldController,
    fontScaleViewModel: FontScaleViewModel,
    showHelp: Boolean,
    onHelpChange: (Boolean) -> Unit,
    showAppearance: Boolean,
    onAppearanceChange: (Boolean) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
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
