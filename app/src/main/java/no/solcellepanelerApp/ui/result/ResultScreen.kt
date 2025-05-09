package no.solcellepanelerApp.ui.result


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import no.solcellepanelerApp.R
import no.solcellepanelerApp.ui.electricity.PriceScreenViewModel
import no.solcellepanelerApp.ui.electricity.PriceUiState
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.handling.LoadingScreen
import no.solcellepanelerApp.ui.map.MapScreenViewModel
import no.solcellepanelerApp.ui.navigation.AppearanceBottomSheet
import no.solcellepanelerApp.ui.navigation.BottomBar
import no.solcellepanelerApp.ui.navigation.HelpBottomSheet
import no.solcellepanelerApp.ui.navigation.TopBar
import no.solcellepanelerApp.ui.reusables.DataCard
import no.solcellepanelerApp.ui.reusables.IconTextRow
import no.solcellepanelerApp.ui.reusables.SavingsMonth_Card
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun ResultScreen(
    navController: NavController,
    viewModel: MapScreenViewModel,
    weatherViewModel: WeatherViewModel,
    fontScaleViewModel: FontScaleViewModel,
    priceScreenViewModel: PriceScreenViewModel,
) {
    val uiState by weatherViewModel.uiState.collectAsState()
    val errorScreen by weatherViewModel.errorScreen.collectAsState()

    // State variables for bottom sheets
    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                navController,
                text = stringResource(R.string.results),
            )
        },
        bottomBar = {
            BottomBar(
                onHelpClicked = { showHelp = true },
                onAppearanceClicked = { showAppearance = true },
                navController = navController
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            when (uiState) {
                UiState.LOADING -> LoadingScreen()
                UiState.ERROR -> errorScreen()
                else -> ResultContent(navController, viewModel, weatherViewModel, priceScreenViewModel)
            }
        }

        HelpBottomSheet(
            navController = navController,
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

@Composable
fun ResultContent(
    navController: NavController,
    viewModel: MapScreenViewModel,
    weatherViewModel: WeatherViewModel,
    priceScreenViewModel: PriceScreenViewModel
) {
    val weatherData by weatherViewModel.weatherData.collectAsState()
    val calc by weatherViewModel.calculationResults.collectAsState()
    var showAllMonths by remember { mutableStateOf(false) }

    // Calculate panel parameters
    val panelArea = viewModel.areaInput.toDouble()
    val efficiency = viewModel.efficiencyInput.toDouble()

    // Get current energy price
    val energyPrice = getCurrentEnergyPrice(priceScreenViewModel)

    // Extract data from weather API
    val airTempData = weatherData["mean(air_temperature P1M)"] ?: emptyArray()

    // Calculate solar panel output
    weatherViewModel.calculateSolarPanelOutput(panelArea, efficiency)

    // Action buttons for savings and month view toggle
    ActionButtonsRow(
        navController = navController,
        yearlyEnergyOutput = calc?.yearlyEnergyOutput ?: 0.0,
        energyPrice = energyPrice,
        showAllMonths = showAllMonths,
        onToggleMonthsView = { showAllMonths = !showAllMonths }
    )

    // Display monthly data
    calc?.let {
        MonthlyDataSection(
            airTempData = airTempData,
            adjustedRadiation = it.adjustedRadiation,
            monthlyEnergyOutput = it.monthlyEnergyOutput,
            monthlyPowerOutput = it.monthlyPowerOutput,
            navController = navController,
            energyPrice = energyPrice,
            showAllMonths = showAllMonths
        )
    }
}

@Composable
fun ActionButtonsRow(
    navController: NavController,
    yearlyEnergyOutput: Double,
    energyPrice: Double,
    showAllMonths: Boolean,
    onToggleMonthsView: () -> Unit
) {
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
                navController.navigate("yearly_savings/$yearlyEnergyOutput/$energyPrice")
            }
        )

        SavingsMonth_Card(
            label = if (showAllMonths)
                stringResource(R.string.show_one_month)
            else
                stringResource(R.string.show_all_months),
            iconRes = R.drawable.baseline_calendar_month_24,
            onClick = onToggleMonthsView
        )
    }
}

@Composable
fun MonthlyDataSection(
    airTempData: Array<Double>,
    adjustedRadiation: List<Double>,
    monthlyEnergyOutput: List<Double>,
    monthlyPowerOutput: List<Double>,
    navController: NavController,
    energyPrice: Double,
    showAllMonths: Boolean
) {
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

    if (showAllMonths) {
        AllMonthsView(
            months = months,
            airTempData = airTempData,
            adjustedRadiation = adjustedRadiation,
            monthlyEnergyOutput = monthlyEnergyOutput,
            monthlyPowerOutput = monthlyPowerOutput,
            navController = navController,
            energyPrice = energyPrice
        )
    } else {
        SingleMonthView(
            months = months,
            airTempData = airTempData,
            adjustedRadiation = adjustedRadiation,
            monthlyEnergyOutput = monthlyEnergyOutput,
            monthlyPowerOutput = monthlyPowerOutput,
            navController = navController,
            energyPrice = energyPrice
        )
    }
}

@Composable
fun SingleMonthView(
    months: List<String>,
    airTempData: Array<Double>,
    adjustedRadiation: List<Double>,
    monthlyEnergyOutput: List<Double>,
    monthlyPowerOutput: List<Double>,
    navController: NavController,
    energyPrice: Double
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedMonthIndex by remember { mutableIntStateOf(0) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight()
    ) {
        // Month dropdown selector
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
                    text = { Text(month, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        selectedMonthIndex = index
                        expanded = false
                    }
                )
            }
        }

        // Display data for selected month
        DataCard(
            month = months[selectedMonthIndex],
            temp = airTempData[selectedMonthIndex],
            adjusted = adjustedRadiation[selectedMonthIndex],
            energy = monthlyEnergyOutput[selectedMonthIndex],
            power = monthlyPowerOutput[selectedMonthIndex],
            navController = navController,
            energyPrice = energyPrice,
            allMonths = false
        )

        // Globe animation and CO2 info
        CO2SavingsDisplay(powerOutput = monthlyPowerOutput[selectedMonthIndex])
    }
}

@Composable
fun CO2SavingsDisplay(powerOutput: Double) {
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
                calculateSavedCO2(powerOutput)
            ),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AllMonthsView(
    months: List<String>,
    airTempData: Array<Double>,
    adjustedRadiation: List<Double>,
    monthlyEnergyOutput: List<Double>,
    monthlyPowerOutput: List<Double>,
    navController: NavController,
    energyPrice: Double
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(months.size) { month ->
            DataCard(
                month = months[month],
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
//animation for globe
@Composable
fun GlobeAnimation() {
    val animationFile = "globe_anim.json"

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(animationFile)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(150.dp)
    )
}

// Helper function to get current energy price
@Composable
fun getCurrentEnergyPrice(priceScreenViewModel: PriceScreenViewModel): Double {
    val priceUiState by priceScreenViewModel.priceUiState.collectAsStateWithLifecycle()

    return when (priceUiState) {
        is PriceUiState.Success -> {
            val prices = (priceUiState as PriceUiState.Success).prices
            val currentHour = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).hour
            prices.find { price -> ZonedDateTime.parse(price.time_start).hour == currentHour }?.NOK_per_kWh
                ?: 0.0
        }
        else -> 0.0
    }
}

// Utility function used to check if calculation are correct
fun calculateMonthlyEnergyOutput(
    avgTemp: List<Double>,
    cloudCover: List<Double>,
    snowCover: List<Double>,
    radiation: List<Double>,
    panelArea: Double,
    efficiency: Double,
    tempCoeff: Double,
): List<Double> {
    return radiation.indices.map { month ->
        val adjustedRadiation =
            radiation[month] * (1 - cloudCover[month] / 8) * (1 - snowCover[month] / 4)
        val tempFactor = 1 + tempCoeff * (avgTemp[month] - 25)
        adjustedRadiation * panelArea * (efficiency / 100.0) * tempFactor
    }
}
//small function to calculate the saved CO2, since it is simple no need to put in a viewmodel
fun calculateSavedCO2(energy: Double): Double {
    val norwayEmissionFactor = 0.018 //0.018 kg CO2/kWh
    return energy * norwayEmissionFactor
}