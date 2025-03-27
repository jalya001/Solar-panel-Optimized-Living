package no.solcellepaneller.ui.additionalInput

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import no.solcellepaneller.ui.map.MapScreenViewModel
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.HelpBottomSheet
import no.solcellepaneller.ui.navigation.InformationBottomSheet
import no.solcellepaneller.ui.navigation.TopBar

@Composable
fun AdditionalInputScreen(viewModel: MapScreenViewModel, navController: NavController) {
    var showHelp by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }
    val coordinates = viewModel.coordinates.value

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = {
            BottomBar(
                onHelpClicked = { showHelp = true },
                onInfoClicked = { showInfo = true },
                onAppearanceClicked = { showAppearance = true },navController
            )

        }    ){ contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Ekstra Inndata")
            coordinates?.let {
                Text("Valgt posisjon:")
                Text("Lat: ${it.first}")
                Text("Lng: ${it.second}")
            } ?: Text("Ingen koordinater valgt")


            Button(onClick = { navController.navigate("map") }) { Text("Tegn Paneler") }

            HelpBottomSheet(visible = showHelp, onDismiss = { showHelp = false })
            InformationBottomSheet(visible = showInfo, onDismiss = { showInfo = false })
            AppearanceBottomSheet(visible = showAppearance, onDismiss = { showAppearance = false })
        }
    }
}
