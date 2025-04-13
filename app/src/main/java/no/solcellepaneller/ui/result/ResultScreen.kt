package no.solcellepaneller.ui.result

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.solcellepaneller.R
import no.solcellepaneller.ui.map.MapScreenViewModel
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.HelpBottomSheet

import no.solcellepaneller.ui.navigation.TopBar
import no.solcellepaneller.ui.font.FontScaleViewModel
import no.solcellepaneller.ui.handling.LoadingScreen

@Composable
fun ResultScreen(navController: NavController, viewModel: MapScreenViewModel, weatherViewModel: WeatherViewModel,    fontScaleViewModel: FontScaleViewModel
) {
    val frostData by weatherViewModel.frostData.collectAsState()
    val radiationData by weatherViewModel.radiationData.collectAsState()
    val radiationList = remember(radiationData) { radiationData.map { it.radiation } }
    val coordinates by viewModel.coordinates.observeAsState()
    val loading by weatherViewModel.isLoading.collectAsState()
    var startloading by remember {  mutableStateOf(false) }
    val slope = viewModel.angleInput.toIntOrNull()
    val panelArea = viewModel.areaInput.toDouble()
    val efficiency = viewModel.efficiencyInput.toDouble()
    val direction = viewModel.directionInput.toInt()
    val months = listOf(
        stringResource(R.string.month_january),
        stringResource(R.string.month_february),
        stringResource(R.string.month_march),
        stringResource(R.string.month_april),
        stringResource(R.string.month_may),
        stringResource(R.string.month_june),
        stringResource(R.string.month_july),
        stringResource(R.string.month_august),
        stringResource(R.string.month_september),
        stringResource(R.string.month_october),
        stringResource(R.string.month_november),
        stringResource(R.string.month_december)
    )

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
            Text(stringResource(id = R.string.results), fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Text("📍 Lat: ${coordinates?.first ?: "N/A"}")
            Text("📍 Long: ${coordinates?.second ?: "N/A"}")
            Text("📏 Areal: ${viewModel.areaInput} m²")
            Text(text = "${stringResource(id = R.string.angle)} ${viewModel.angleInput}°")
            Text(text = "${stringResource(id = R.string.direction)}  ${viewModel.directionInput} °")
            Text(text = "${stringResource(id = R.string.effectivity)} ${viewModel.efficiencyInput} %")
            if(loading){
                startloading = true
            }
            else if (frostData.isNotEmpty()) {
                startloading = false
                val snowCoverData = frostData["mean(snow_coverage_type P1M)"] ?: emptyArray()
                val airTempData = frostData["mean(air_temperature P1M)"] ?: emptyArray()
                val cloudCoverData = frostData["mean(cloud_area_fraction P1M)"] ?: emptyArray()

                val calculatedMonthly = calculateMonthlyEnergyOutput(
                    airTempData, cloudCoverData, snowCoverData, panelArea, efficiency, -0.44, radiationList
                )

                val adjustedRadiation = mutableListOf<Double>()
                val monthlyEnergyOutput = radiationList.indices.map { month ->
                    adjustedRadiation.add(radiationList[month] * (1 - (cloudCoverData[month] / 8)) * (1 - (snowCoverData[month] / 4)))
                    val tempFactor = 1 + (-0.44) * (airTempData[month] - 25)
                    adjustedRadiation[month] * panelArea * (efficiency / 100.0) * tempFactor
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
                                Text( stringResource(id = R.string.month)+ " " + months[month], fontWeight = FontWeight.Bold)
                                Text(stringResource(id = R.string.global_radiation)+ " %.2f".format(radiationList[month]))
                                Text(stringResource(id = R.string.avg_cloud_cover)+ " %.2f".format(cloudCoverData[month] / 8))
                                Text(stringResource(id = R.string.avg_snow_cover)+ " %.2f".format(snowCoverData[month] / 4))
                                Text(stringResource(id = R.string.temp_factor)+ " %.2f °C".format(1 + (-0.44) * (airTempData[month] - 25)))
                                Text(stringResource(id = R.string.adj_radiation)+ " %.2f kWh/m²\n".format(adjustedRadiation[month]))
                                Text(
                                    stringResource(id = R.string.estimated_energy_prod).format(
                                        monthlyEnergyOutput[month]
                                    ), fontWeight = FontWeight.Bold
                                )
                                Text(stringResource(id = R.string.estimated_powerpr_hour) + " %.2f kW" .format(monthlyPowerOutput[month]))
                            }
                        }
                    }
                }


            } else {
                Text(stringResource(id = R.string.data_not_added))
                //Text("⚠ No snow coverage data available." )
            }

            Button(onClick = {
                coordinates?.let {
                    weatherViewModel.fetchFrostData(it.first, it.second, listOf(
                        "mean(snow_coverage_type P1M)", "mean(air_temperature P1M)", "mean(cloud_area_fraction P1M)"
                    ))
                    if (slope != null) {
                        weatherViewModel.fetchRadiationInfo(it.first, it.second, slope,direction)
                    }
                }
            }) {
                Text(stringResource(id = R.string.get_data))
            }
            if (startloading) {
            Column {
//                Text(stringResource(id = R.string.loading), fontWeight = FontWeight.Bold)
                LoadingScreen()
            }}

            HelpBottomSheet(visible = showHelp, navController = navController, onDismiss = { showHelp = false })
AppearanceBottomSheet(
    visible = showAppearance,
    onDismiss = { showAppearance = false },
    fontScaleViewModel = fontScaleViewModel
)        }
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
