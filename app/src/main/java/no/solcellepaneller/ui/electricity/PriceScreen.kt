package no.solcellepaneller.ui.electricity

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.solcellepaneller.data.homedata.ElectricityPriceRepository
import no.solcellepaneller.model.electricity.ElectricityPrice
import no.solcellepaneller.model.electricity.Region
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.HelpBottomSheet
import no.solcellepaneller.ui.navigation.TopBar
import java.time.ZoneId
import java.time.ZonedDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceScreen(
    repository: ElectricityPriceRepository,navController: NavController
) {
    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    var selectedRegion by remember { mutableStateOf(Region.OSLO) }
    val viewModel: PriceScreenViewModel = viewModel(
        factory = PriceViewModelFactory(repository, selectedRegion.regionCode),
        key = selectedRegion.regionCode
    )

    val priceUiState by viewModel.priceUiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = {
            BottomBar(
                onHelpClicked = { showHelp = true },
                onAppearanceClicked = { showAppearance = true },navController
            ) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RegionDropdown(selectedRegion) { newRegion ->
                selectedRegion = newRegion
            }
            Spacer(modifier = Modifier.height(8.dp))
            when (priceUiState) {
                is PriceUiState.Loading -> LoadingScreen()
                is PriceUiState.Error -> ErrorScreen()
                is PriceUiState.Success -> {
                    val prices = (priceUiState as PriceUiState.Success).prices
                    PriceList(prices)
                }
            }

            HelpBottomSheet(visible = showHelp, onDismiss = { showHelp = false })
            AppearanceBottomSheet(visible = showAppearance, onDismiss = { showAppearance = false })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionDropdown(
    selectedRegion: Region,
    onRegionSelected: (Region) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = selectedRegion.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Velg distrikt", color = Color.Blue) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Region.entries.forEach { region ->
                DropdownMenuItem(
                    text = { Text(region.displayName) },
                    onClick = {
                        onRegionSelected(region)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PriceList(prices: List<ElectricityPrice>) {
    val currentHour = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).hour

    val currentPrice = prices.find { price ->
        val startTime = ZonedDateTime.parse(price.time_start)
        startTime.hour == currentHour
    } ?: run {
        Log.e("ERROR", "Fant ingen pris for nåværende time!")
        null
    }

    val highestPrice = prices.maxByOrNull { it.NOK_per_kWh }
    val lowestPrice = prices.minByOrNull { it.NOK_per_kWh }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        lowestPrice?.let {
            Text(
                text = "Laveste pris i dag: ${it.NOK_per_kWh} NOK/kWh",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tid: ${it.getTimeRange()}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (currentPrice != null) {
            Text(
                text = "Pris nå: ${currentPrice.NOK_per_kWh} NOK/kWh",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tid: ${currentPrice.getTimeRange()}",
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            Text(
                text = "Ingen pris tilgjengelig for nåværende time",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        highestPrice?.let {
            Text(
                text = "Høyeste pris i dag: ${it.NOK_per_kWh} NOK/kWh",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tid: ${it.getTimeRange()}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Laster inn data, vennligst vent...")
        }
    }
}

@Composable
fun ErrorScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Noe gikk galt! Prøv igjen senere.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
