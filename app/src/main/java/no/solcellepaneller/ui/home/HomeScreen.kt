package no.solcellepaneller.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import no.solcellepaneller.App
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.HelpBottomSheet
import no.solcellepaneller.ui.navigation.InformationBottomSheet
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.TopBar
import no.solcellepaneller.ui.theme.SolcellepanellerTheme

@Composable
fun HomeScreen(navController: NavController) {
    var showHelp by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomBar(
            onHelpClicked = { showHelp = true },
            onInfoClicked = { showInfo = true },
            onAppearanceClicked = { showAppearance = true }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Hjem")
            Button(onClick = { navController.navigate("install_panels") }) { Text("Installer Paneler") }
            Button(onClick = { navController.navigate("saved_locations") }) { Text("Lagrede Posisjoner") }
            Button(onClick = { navController.navigate("prices") }) { Text("Priser") }

            HelpBottomSheet(visible = showHelp, onDismiss = { showHelp = false })
            InformationBottomSheet(visible = showInfo, onDismiss = { showInfo = false })
            AppearanceBottomSheet(visible = showAppearance, onDismiss = { showAppearance = false })
        }
    }
}
//Funker ikke lenger pga måten isDark er implementert
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "DefaultPreviewDark"
//)
@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    name = "DefaultPreviewLight"
    name = "DefaultPreview"
)
@Composable
fun AppPreview() {
    SolcellepanellerTheme {
        App()
    }
}