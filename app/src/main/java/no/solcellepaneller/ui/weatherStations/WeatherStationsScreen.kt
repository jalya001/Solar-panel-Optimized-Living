package no.solcellepaneller.ui.weatherStations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.TopBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.HelpBottomSheet
import no.solcellepaneller.ui.navigation.InformationBottomSheet
import no.solcellepaneller.ui.map.MapScreenViewModel


@Composable
fun WeatherStationsScreen(viewModel: MapScreenViewModel,navController: NavController) {
    var showHelp by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }
    val coordinates by viewModel.coordinates.observeAsState()


    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = {
            BottomBar(
                onHelpClicked = { showHelp = true },
                onInfoClicked = { showInfo = true },
                onAppearanceClicked = { showAppearance = true }) } ){ contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Værstasjon")
            Text("Dine koordinater:")
            coordinates?.let {
                Text("Lat: ${it.first}")
                Text("Lon: ${it.second}")
            } ?: Text("Ingen koordinater valgt")


            Button(onClick = { navController.navigate("additional_input") }) { Text("Gå til Ekstra Inndata") }

            HelpBottomSheet(visible = showHelp, onDismiss = { showHelp = false })
            InformationBottomSheet(visible = showInfo, onDismiss = { showInfo = false })
            AppearanceBottomSheet(visible = showAppearance, onDismiss = { showAppearance = false })
        }
    }
}
