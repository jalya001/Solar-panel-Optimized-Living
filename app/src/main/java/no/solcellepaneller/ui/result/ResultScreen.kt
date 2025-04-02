package no.solcellepaneller.ui.result

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val loading by weatherViewModel.isLoading.collectAsState()
    var startloading by remember {  mutableStateOf(false) }
    val slope = viewModel.angleInput.toIntOrNull()
    val panelArea = viewModel.areaInput.toDouble()
    val efficiency = viewModel.efficiencyInput.toDouble()
    val months = arrayOf("Januar", "Februar", "Mars", "April", "Mai", "Juni",
        "Juli", "August", "September", "Oktober", "November", "Desember")
    val daysInMonth = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = {
            BottomBar(
                onHelpClicked = { showHelp = true },
                onAppearanceClicked = { showAppearance = true },
                navController = navController
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text("🔎 Resultater", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Text("📍 Lat: ${coordinates?.first ?: "N/A"}")
            Text("📍 Long: ${coordinates?.second ?: "N/A"}")
            Text("📏 Areal: ${viewModel.areaInput} m²")
            Text("📐 Vinkel: ${viewModel.angleInput}°")
            Text("🧭 Retning: ${viewModel.directionInput}")
            Text("⚡ Effektivitet: ${viewModel.efficiencyInput} %")
            if(loading){
                startloading = true
            }
            else if (frostData.containsKey("mean(snow_coverage_type P1M)")) {
                startloading = false
                val snowCoverData = frostData["mean(snow_coverage_type P1M)"] ?: emptyArray()
                val airTempData = frostData["mean(air_temperature P1M)"] ?: emptyArray()
                val cloudCoverData = frostData["mean(cloud_area_fraction P1M)"] ?: emptyArray()

                val calculatedMonthly = calculateMonthlyEnergyOutput(
                    airTempData, cloudCoverData, snowCoverData, panelArea, efficiency, -0.44, radiationList
                )
                val monthlyEnergyOutput = radiationList.indices.map { month ->
                    val adjustedRadiation = radiationList[month] * (1 - (cloudCoverData[month] / 8)) * (1 - (snowCoverData[month] / 4))
                    val tempFactor = 1 + (-0.44) * (airTempData[month] - 25)
                    adjustedRadiation * panelArea * (efficiency / 100.0) * tempFactor
                }


                val monthlyPowerOutput = monthlyEnergyOutput.mapIndexed { index, energyKWh ->
                    val totalHours = daysInMonth[index] * 24 // Total hours in the month
                    energyKWh / totalHours // Convert kWh to kW
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(radiationList.indices.toList()) { month ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text("📅 Month: ${months[month]}", fontWeight = FontWeight.Bold)
                                Text("☼ Global Radiation: %.2f kWh/m²".format(radiationList[month]))
                                Text("☁️ Average Cloud Cover: %.2f".format(cloudCoverData[month] / 8))
                                Text("☃ Average Snow Cover: %.2f".format(snowCoverData[month] / 4))
                                Text("🔥 Temperature Factor: %.2f".format(1 + (-0.44) * (airTempData[month] - 25)))
                                Text("⚡ Adjusted Radiation: %.2f kWh/m²".format(monthlyEnergyOutput[month]))
                                Text(
                                    "🔋 Estimated Energy Produced: %.2f kWh".format(
                                        monthlyEnergyOutput[month]
                                    ), fontWeight = FontWeight.Bold
                                )
                                Text("📊 Estimated Power Produced per Hour: %.2f kW".format(monthlyPowerOutput[month]))
                            }
                        }
                    }
                }


            } else {
                Text("⚠ No snow coverage data available." )
            }

//            Button(onClick = {
//                navController.navigate("home") {
//                    popUpTo("home") { inclusive = true }
//                }
//            }) { Text("🏠 Tilbake til Start") }

            Button(onClick = {
                coordinates?.let {
                    weatherViewModel.fetchFrostData(it.first, it.second, listOf(
                        "mean(snow_coverage_type P1M)", "mean(air_temperature P1M)", "mean(cloud_area_fraction P1M)"
                    ))
                    if (slope != null) {
                        weatherViewModel.fetchRadiationInfo(it.first, it.second, slope)
                    }
                }
            }) {
                Text("📡 Hent Data")
            }
            if (startloading) {
            Column {
                Text("⏳ Laster inn data, vennligst vent...", fontWeight = FontWeight.Bold)
            }}

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
        val adjustedRadiation = radiation[month] * (1 - cloudCover[month] / 8) * (1 - snowCover[month] / 4)
        val tempFactor = 1 + tempCoeff * (avgTemp[month] - 25)
        adjustedRadiation * panelArea * (efficiency / 100.0) * tempFactor
    }
}
