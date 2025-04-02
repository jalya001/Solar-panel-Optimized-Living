package no.solcellepaneller.ui.result

import android.util.Log
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
import androidx.compose.runtime.livedata.observeAsState
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
fun ResultScreen(navController: NavController, viewModel: MapScreenViewModel, weatherViewModel: WeatherViewModel ) {
    val frostData by weatherViewModel.frostData.collectAsState()
    val radiationData by weatherViewModel.radiationData.collectAsState()
    val radiationList = remember(radiationData) { radiationData.map { it.radiation } }
    val coordinates by viewModel.coordinates.observeAsState()
    val slope = viewModel.angleInput.toIntOrNull()
    val panelArea= viewModel.areaInput.toDouble()
    val efficiency=viewModel.efficiencyInput.toDouble()
    var showHelp by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }


    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = {
            BottomBar(
                onHelpClicked = { showHelp = true },
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
            if (frostData.containsKey("mean(snow_coverage_type P1M)")) {
                val snowCoverData = frostData["mean(snow_coverage_type P1M)"] ?: emptyArray()
                val airTempData =frostData["mean(air_temperature P1M)"]?: emptyArray()
                val cloudCoverData = frostData["mean(cloud_area_fraction P1M)"]?: emptyArray()

                val calcultedMonthly= calculateMonthlyEnergyOutput(airTempData,cloudCoverData,snowCoverData,panelArea,efficiency,-0.44,radiationList)

                Log.d("ResultScreen", "Snow coverage data: $snowCoverData")
                Log.d("ResultScreen", "Snow coverage data: $airTempData")
                Log.d("ResultScreen", "Snow coverage data: $cloudCoverData")
                Text("Monthly Energy Output: ${calcultedMonthly.joinToString(", ")}")
                //Text("Snow Coverage: ${snowCoverData.joinToString(", ")}")
            } else {
                Text("No snow coverage data")
            }
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
                    coordinates?.let {
                        weatherViewModel.fetchFrostData(it.first,it.second, listOf("mean(snow_coverage_type P1M)","mean(air_temperature P1M)", "mean(cloud_area_fraction P1M)"))
                        if (slope != null) {
                            weatherViewModel.fetchRadiationInfo(it.first, it.second,slope)
                            //val radiationList = radiationData.map { it.radiation }
                        }
                    }
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
                    Text("No frost data")
                }

                Button(onClick = {
                    coordinates?.let {
                        if (slope != null) {
                            weatherViewModel.fetchRadiationInfo(it.first, it.second, slope)
                        }
                    }
                }) {
                    Text("test pvgis")
                }

                if (radiationData.isNotEmpty()) {
                    Text(radiationData.joinToString(", "))
                    Text(radiationData.toString())
                   // val radiationList = radiationData.map { it.radiation } // Extract radiation values

                } else {
                    Text("No pvgis data")
                }
            }
            HelpBottomSheet(visible = showHelp, navController = navController, onDismiss = { showHelp = false })
           
            AppearanceBottomSheet(visible = showAppearance, onDismiss = { showAppearance = false })
        }
    }
}


fun calculateMonthlyEnergyOutput(
    avgTemp: Array<Double>,
    cloudCover: Array<Double>,
    snowCover: Array<Double>,
    panelArea: Double,
    efficiency: Double,
    tempCoeff: Double,
    radiation: List<Double>
): List<Double> {
    return radiation.indices.map { month ->
        val adjustedRadiation = radiation[month] * (1 - cloudCover[month]/8) * (1 - snowCover[month]/4)
        val tempFactor = 1 + tempCoeff * (avgTemp[month] - 25)
        adjustedRadiation * panelArea * (efficiency / 100.0) * tempFactor
    }
}