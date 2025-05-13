package no.solcellepanelerApp.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.navigation.NavController
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.reusables.SimpleTutorialOverlay

@Composable

fun AppScaffold(
    navController: NavController,
    controller: AppScaffoldController,
    fontScaleViewModel: FontScaleViewModel,
    content: @Composable (PaddingValues) -> Unit,
) {
    if (controller.showOverlay) {
        val title = stringResource(controller.overlayTitle)
        val body = stringResource(controller.overlayBody)
        val message = buildAnnotatedString {
            withStyle(style = MaterialTheme.typography.titleLarge.toSpanStyle()) {
                append("$title\n\n")
            }
            withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {
                append(body)
            }
        }

        SimpleTutorialOverlay(
            onDismiss = { controller.showOverlay = false },
            message = message
        )
    }

    Scaffold(
        topBar = {
            controller.topBarContent?.invoke()
                ?: controller.topBarTitle?.let { TopBar(navController, text = it) }
        },
        bottomBar = {
            if (controller.isBottomBarVisible) {
                BottomBar(
                    navController = navController,
                    onHelpClicked = { controller.showHelp = true },
                    onAppearanceClicked = { controller.showAppearance = true }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = controller.snackbarHostState)
        }
    ) { padding ->
        content(padding)

        HelpBottomSheet(
            navController = navController,
            visible = controller.showHelp,
            onDismiss = { controller.showHelp = false }
        )

        AppearanceBottomSheet(
            visible = controller.showAppearance,
            onDismiss = { controller.showAppearance = false },
            fontScaleViewModel = fontScaleViewModel
        )
    }
}
