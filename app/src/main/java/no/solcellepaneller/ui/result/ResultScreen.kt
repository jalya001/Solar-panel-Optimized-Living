package no.solcellepaneller.ui.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

import no.solcellepaneller.ui.navigation.TopBar

@Composable
fun ResultScreen(navController: NavController, viewModel: MapScreenViewModel, frostViewModel: FrostViewModel = FrostViewModel()) {
    val frostData by frostViewModel.frostData.collectAsState()

    var showHelp by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = {
            BottomBar(
                onHelpClicked = { showHelp = true },
                onInfoClicked = { showInfo = true },
                onAppearanceClicked = { showAppearance = true },navController
            ) }
    ){ contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Resultater")
            Text(text = "Lat: ${viewModel.coordinates.value?.first}")
            Text(text = "Long: ${viewModel.coordinates.value?.second}")
            Text(text = "Areal: ${viewModel.areaInput} m²")
            Text(text = "Vinkel: ${viewModel.angleInput}°")
            Text(text = "Retning: ${viewModel.directionInput}")
            Text(text = "Effektivitet: ${viewModel.efficiencyInput} %")

            Button(onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            }) { Text("Tilbake til Start") }


            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    frostViewModel.loadFrostData(59.91, 10.75, listOf("mean(snow_coverage_type P1M)","mean(air_temperature P1M)", "mean(cloud_area_fraction P1M)"))
                }) {
                    Text("test frost")
                }

                if (frostData.isNotEmpty()) {
                    LazyColumn {
                        items(frostData.entries.toList()) { (key, values) ->
                            Text("$key: ${values.joinToString(", ")}")
                        }
                    }
                } else {
                    Text("No data loaded")
                }
            }
            HelpBottomSheet(visible = showHelp, onDismiss = { showHelp = false })
           
            AppearanceBottomSheet(visible = showAppearance, onDismiss = { showAppearance = false })
        }
    }
}

