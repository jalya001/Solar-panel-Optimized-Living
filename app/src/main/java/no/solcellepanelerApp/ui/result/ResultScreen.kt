package no.solcellepanelerApp.ui.result

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import no.solcellepanelerApp.R
import no.solcellepanelerApp.ui.handling.LoadingScreen
import no.solcellepanelerApp.ui.reusables.DataCard
import no.solcellepanelerApp.ui.reusables.IconTextRow
import no.solcellepanelerApp.ui.reusables.SavingsMonth_Card

@Composable
fun ResultScreen(
    navController: NavController,
    contentPadding: PaddingValues,
    resultViewModel: ResultViewModel = ResultViewModel(),
) {
    val weatherData by resultViewModel.weatherData.collectAsState()
    val errorScreen by resultViewModel.errorScreen.collectAsState()
    val calc by resultViewModel.calculationResults.collectAsState()
    Log.d("CALC", calc.toString())
    val uiState by resultViewModel.uiState.collectAsState()
//    val direction = viewModel.directionInput.toInt()
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


//    val selectedRegion = viewModel.selectedRegion


    //val priceUiState by priceScreenViewModel.priceUiState.collectAsStateWithLifecycle()
/*
    val energyPrice = when (priceUiState) {
        is PriceUiState.Success -> {
            val prices = (priceUiState as PriceUiState.Success).prices
            val currentHour = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).hour
            prices.find { price -> ZonedDateTime.parse(price.time_start).hour == currentHour }?.NOK_per_kWh
                ?: 0.0
        }

        else -> 0.0
    }*/

    var showAllMonths by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        when (uiState) {
            ResultViewModel.UiState.LOADING -> {
                LoadingScreen()
            }

            ResultViewModel.UiState.ERROR -> {
                errorScreen()
            }

            else -> {

                val snowCoverData = weatherData["mean(snow_coverage_type P1M)"] ?: emptyArray()
                val airTempData = weatherData["mean(air_temperature P1M)"] ?: emptyArray()
                val cloudCoverData =
                    weatherData["mean(cloud_area_fraction P1M)"] ?: emptyArray()
                val radiationData = weatherData["mean(PVGIS_radiation P1M)"] ?: emptyArray()



                val energyPrice = 1.0 // temp

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SavingsMonth_Card(
                        label = stringResource(R.string.yearly_savings_label),
                        iconRes = R.drawable.baseline_attach_money_24,
                        onClick = {
                            navController.navigate("yearly_savings/${calc?.yearlyEnergyOutput}/$energyPrice")
                        }
                    )

                    SavingsMonth_Card(
                        label = if (showAllMonths) stringResource(R.string.show_one_month)
                        else stringResource(R.string.show_all_months),
                        iconRes = R.drawable.baseline_calendar_month_24,
                        onClick = {
                            showAllMonths = !showAllMonths
                        }
                    )
                }

                calc?.let {
                    MonthDataDisplay(
                        cloudCoverData = cloudCoverData,
                        snowCoverData = snowCoverData,
                        airTempData = airTempData,
                        radiationData = radiationData,
                        adjustedRadiation = it.adjustedRadiation,
                        monthlyEnergyOutput = it.monthlyEnergyOutput,
                        monthlyPowerOutput = it.monthlyPowerOutput,
                        months = months,
                        navController = navController,
                        energyPrice = energyPrice,
                        showAllMonths = showAllMonths
                    )
                }
            }
        }
    }
}


@Composable
fun MonthDataDisplay(
    cloudCoverData: Array<Double>,
    snowCoverData: Array<Double>,
    airTempData: Array<Double>,
    radiationData: Array<Double>,
    adjustedRadiation: List<Double>,
    monthlyEnergyOutput: List<Double>,
    monthlyPowerOutput: List<Double>,
    months: List<String>,
    navController: NavController,
    energyPrice: Double,
    showAllMonths: Boolean,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedMonthIndex by remember { mutableIntStateOf(0) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight()
    ) {

        if (!showAllMonths) {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier) {
                IconTextRow(
                    R.drawable.baseline_calendar_month_24,
                    text = stringResource(R.string.selected_month, months[selectedMonthIndex])
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
                month = months[selectedMonthIndex],
                radiation = radiationData[selectedMonthIndex],
                cloud = cloudCoverData[selectedMonthIndex],
                snow = snowCoverData[selectedMonthIndex],
                temp = airTempData[selectedMonthIndex],
                adjusted = adjustedRadiation[selectedMonthIndex],
                energy = monthlyEnergyOutput[selectedMonthIndex],
                power = monthlyPowerOutput[selectedMonthIndex],
                navController = navController,
                energyPrice = energyPrice,
                allMonths = false
            )

            Column(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlobeAnimation()
                Text(
                    stringResource(
                        id = R.string.savedGlobe,
                        //calculateSavedCO2(monthlyPowerOutput[selectedMonthIndex])
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

        } else {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(months.size) { month ->
                    DataCard(
                        month = months[month],
                        radiation = radiationData[month],
                        cloud = cloudCoverData[month],
                        snow = snowCoverData[month],
                        temp = airTempData[month],
                        adjusted = adjustedRadiation[month],
                        energy = monthlyEnergyOutput[month],
                        power = monthlyPowerOutput[month],
                        navController = navController,
                        energyPrice = energyPrice,
                        allMonths = true
                    )
                }
            }
        }
    }
}

@Composable
fun GlobeAnimation() {

    val animationFile = "globe_anim.json"

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
            .size(150.dp)
//            .background(Color.Blue)
    )
}