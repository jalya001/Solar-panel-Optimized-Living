package no.solcellepaneller.ui.result

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import no.solcellepaneller.R
import no.solcellepaneller.data.homedata.ElectricityPriceRepository
import no.solcellepaneller.model.electricity.Region
import no.solcellepaneller.ui.electricity.PriceScreenViewModel
import no.solcellepaneller.ui.electricity.PriceUiState
import no.solcellepaneller.ui.electricity.PriceViewModelFactory
import no.solcellepaneller.ui.font.FontScaleViewModel
import no.solcellepaneller.ui.handling.LoadingScreen
import no.solcellepaneller.ui.map.MapScreenViewModel
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.HelpBottomSheet
import no.solcellepaneller.ui.navigation.TopBar
import no.solcellepaneller.ui.reusables.DataCard
import no.solcellepaneller.ui.reusables.IconTextRow
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun ResultScreen(
    navController: NavController, viewModel: MapScreenViewModel, weatherViewModel: WeatherViewModel,
    fontScaleViewModel: FontScaleViewModel, priceScreenViewModel: ElectricityPriceRepository,
) {
    val frostData by weatherViewModel.frostData.collectAsState()
    Log.d("frostData", frostData.toString())
    val radiationData by weatherViewModel.radiationData.collectAsState()
    val radiationList = remember(radiationData) { radiationData.map { it.radiation } }
    val loading by weatherViewModel.isLoading.collectAsState()
    var startloading by remember { mutableStateOf(false) }
    val panelArea = viewModel.areaInput.toDouble()
    val efficiency = viewModel.efficiencyInput.toDouble()

    val months = arrayOf(
        "Januar", "Februar", "Mars", "April", "Mai", "Juni",
        "Juli", "August", "September", "Oktober", "November", "Desember"
    )
    val daysInMonth = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    var selectedRegion by remember { mutableStateOf(Region.OSLO) }

    val priceScreenViewModel: PriceScreenViewModel = viewModel(
        factory = PriceViewModelFactory(priceScreenViewModel, selectedRegion.regionCode),
        key = selectedRegion.regionCode
    )

    val priceUiState by priceScreenViewModel.priceUiState.collectAsStateWithLifecycle()

    val energyPrice = when (priceUiState) {
        is PriceUiState.Success -> {
            val prices = (priceUiState as PriceUiState.Success).prices
            val currentHour = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).hour
            prices.find { price -> ZonedDateTime.parse(price.time_start).hour == currentHour }?.NOK_per_kWh
                ?: 0.0
        }

        else -> 0.0
    }

    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar(navController, text = stringResource(R.string.results)) },
        bottomBar = {
            BottomBar(
                onHelpClicked = { showHelp = true },
                onAppearanceClicked = { showAppearance = true },
                navController = navController
            )
        }
    ) { contentPadding ->
        Column {

            Column(
                modifier = Modifier
                    .padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                if (loading) {
                    startloading = true
                } else if (frostData.isNotEmpty()) {
                    startloading = false
                    val snowCoverData = frostData["mean(snow_coverage_type P1M)"] ?: emptyArray()
                    val airTempData = frostData["mean(air_temperature P1M)"] ?: emptyArray()
                    val cloudCoverData = frostData["mean(cloud_area_fraction P1M)"] ?: emptyArray()

                    val calculatedMonthly = calculateMonthlyEnergyOutput(
                        airTempData,
                        cloudCoverData,
                        snowCoverData,
                        panelArea,
                        efficiency,
                        -0.44,
                        radiationList
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

                    var yearlyEnergyOutput = 0.0
                    for (nums in 0..11) {
                        yearlyEnergyOutput += monthlyEnergyOutput[nums]
                    }
                    Button(onClick = {
                        navController.navigate("yearly_savings/${yearlyEnergyOutput}/$energyPrice")
                    }) {
                        Text("Show yearly savings")
                    }

                    MonthDataDisplay(
                        radiationList = radiationList,
                        cloudCoverData = cloudCoverData,
                        snowCoverData = snowCoverData,
                        airTempData = airTempData,
                        adjustedRadiation = adjustedRadiation,
                        monthlyEnergyOutput = monthlyEnergyOutput,
                        monthlyPowerOutput = monthlyPowerOutput,
                        months = months,
                        navController = navController
                    )
                }

            }

            if (startloading) {
                Column {
                    LoadingScreen()
                }
            }

            HelpBottomSheet(
                visible = showHelp,
                onDismiss = { showHelp = false },
            )
            AppearanceBottomSheet(
                visible = showAppearance,
                onDismiss = { showAppearance = false },
                fontScaleViewModel = fontScaleViewModel
            )
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
    radiation: List<Double>,
): List<Double> {
    return radiation.indices.map { month ->
        val adjustedRadiation =
            radiation[month] * (1 - cloudCover[month] / 8) * (1 - snowCover[month] / 4)
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
    months: Array<String>,
    navController: NavController,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedMonthIndex by remember { mutableStateOf(0) }
    var showAllMonths by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight()
    ) {

        Button(onClick = { showAllMonths = !showAllMonths }) {
            Text(if (showAllMonths) "Show One Month" else "Show All Months")
        }

        if (!showAllMonths) {
            Button(onClick = { expanded = true }) {
                IconTextRow(
                    R.drawable.baseline_calendar_month_24,
                    text = "Måned: ${months[selectedMonthIndex]}"
                )
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
            DataCard(
                month = "",
//                month = months[selectedMonthIndex],
                radiation = radiationList[selectedMonthIndex],
                cloud = cloudCoverData[selectedMonthIndex],
                snow = snowCoverData[selectedMonthIndex],
                temp = airTempData[selectedMonthIndex],
                adjusted = adjustedRadiation[selectedMonthIndex],
                energy = monthlyEnergyOutput[selectedMonthIndex],
                power = monthlyPowerOutput[selectedMonthIndex],
                navController = navController,
            )

            Column {
                SunAnimation(monthlyEnergyOutput[selectedMonthIndex])
            }

        } else {
            Button(
                onClick = {
                    navController.navigate("savings")
                },

                ) {
                Text("Show yearly savings")
            }
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally
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
                        navController = navController,
                    )
                }
            }
        }
    }
}

@Composable
fun SunAnimation(value: Double) {
    val animationFile = when {
        value < 50.0 -> "solar_verylow.json"
        value in 50.0..500.0 -> "solar_low.json"
        value in 500.0..3000.0 -> "solar_half.json"
        value > 3000.0 -> "solar_full.json"
        else -> "solar_verylow.json" // Default animation
    }

    // Force new composition when value changes
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(animationFile)
    )

    // Reset animation state when value changes
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        // Add a key to restart animation when value changes

    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .height(400.dp)
            .fillMaxWidth()
    )
    Log.d("SunAnimation", "Animating with value: $value")
}
