package no.solcellepaneller.ui.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.solcellepaneller.R
import no.solcellepaneller.ui.map.MapScreenViewModel
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.HelpBottomSheet

import no.solcellepaneller.ui.navigation.TopBar
import no.solcellepaneller.ui.font.FontScaleViewModel
import no.solcellepaneller.ui.handling.LoadingScreen
import no.solcellepaneller.ui.reusables.DataCard
import no.solcellepaneller.ui.reusables.MyCard

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
    val months = arrayOf("Januar", "Februar", "Mars", "April", "Mai", "Juni",
        "Juli", "August", "September", "Oktober", "November", "Desember")
    val daysInMonth = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar(navController, text =stringResource(R.string.results)) },
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
            Row {
                MyCard(
                    text = "📏 Areal: ${viewModel.areaInput} m²",
                    style = "",
                    modifier = Modifier.weight(1f).height(120.dp)
                )
                MyCard(
                    text = "${stringResource(id = R.string.angle)} ${viewModel.angleInput}°",
                    style = "",
                    modifier = Modifier.weight(1f).height(120.dp)
                )
                MyCard(
                    text = "${stringResource(id = R.string.effectivity)} ${viewModel.efficiencyInput} %",
                    style = "",
                    modifier = Modifier.weight(1f).height(120.dp)
                )
            }
//            Text("📍 Lat: ${coordinates?.first ?: "N/A"}")
//            Text("📍 Long: ${coordinates?.second ?: "N/A"}")

            //Trengs drection?
//            Text(text = "${stringResource(id = R.string.direction)}  ${viewModel.directionInput}")

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

                var expanded by remember { mutableStateOf(false) }
                var selectedMonthIndex by remember { mutableStateOf(0) }

                Box(modifier = Modifier.fillMaxWidth()) {

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        months.forEachIndexed { index, month ->
                            DropdownMenuItem(
                                text = { Text(month) },
                                onClick = {
                                    selectedMonthIndex = index
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                MonthDataDisplay(
                    radiationList = radiationList,
                    cloudCoverData =cloudCoverData,
                    snowCoverData = snowCoverData,
                    airTempData = airTempData,
                    adjustedRadiation = adjustedRadiation,
                    monthlyEnergyOutput = monthlyEnergyOutput,
                    monthlyPowerOutput = monthlyPowerOutput,
                    months = months
                )

                MyCard("*VISUALISERING*")


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
                        weatherViewModel.fetchRadiationInfo(it.first, it.second, slope)
                    }
                }
            }) {
                Text(stringResource(id = R.string.get_data))
            }
            if (startloading) {
            Column {
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

@Composable
fun MonthDataDisplay(
    radiationList: List<Double>,
    cloudCoverData: Array<Double>,
    snowCoverData: Array<Double>,
    airTempData: Array<Double>,
    adjustedRadiation: List<Double>,
    monthlyEnergyOutput: List<Double>,
    monthlyPowerOutput: List<Double>,
    months: Array<String> // ["January", "February", ..., "December"]
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedMonthIndex by remember { mutableStateOf(0) }
    var showAllMonths by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (!showAllMonths) {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(text = "📅 ${months[selectedMonthIndex]}", color = MaterialTheme.colorScheme.tertiary)
                }
            }

            Button(onClick = { showAllMonths = !showAllMonths }) {
                Text(if (showAllMonths) "Show One Month" else "Show All Months")
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            months.forEachIndexed { index, month ->
                DropdownMenuItem(
                    text = { Text(month) },
                    onClick = {
                        selectedMonthIndex = index
                        expanded = false
                    }
                )
            }
        }

        if (showAllMonths) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(months.size) { month ->
                    DataCard(
                        month = months[month],
                        radiation = radiationList[month],
                        cloud = cloudCoverData[month],
                        snow = snowCoverData[month],
                        temp = airTempData[month],
                        adjusted = adjustedRadiation[month],
                        energy = monthlyEnergyOutput[month],
                        power = monthlyPowerOutput[month],
                        isMultiMonth = true
                    )
                }
            }
        } else {
            DataCard(
                month = months[selectedMonthIndex],
                radiation = radiationList[selectedMonthIndex],
                cloud = cloudCoverData[selectedMonthIndex],
                snow = snowCoverData[selectedMonthIndex],
                temp = airTempData[selectedMonthIndex],
                adjusted = adjustedRadiation[selectedMonthIndex],
                energy = monthlyEnergyOutput[selectedMonthIndex],
                power = monthlyPowerOutput[selectedMonthIndex],
            )
        }
    }
}
