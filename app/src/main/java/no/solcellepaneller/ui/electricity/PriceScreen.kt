package no.solcellepaneller.ui.electricity

import android.app.Activity
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.solcellepaneller.data.homedata.ElectricityPriceRepository
import no.solcellepaneller.data.location.LocationService
import no.solcellepaneller.model.electricity.Region
import no.solcellepaneller.ui.font.FontScaleViewModel
import no.solcellepaneller.ui.handling.ErrorScreen
import no.solcellepaneller.ui.handling.LoadingScreen
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.HelpBottomSheet
import no.solcellepaneller.ui.navigation.TopBar

@Composable
fun PriceScreen(
    repository: ElectricityPriceRepository,
    navController: NavController,
    fontScaleViewModel: FontScaleViewModel
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    var selectedRegion by remember { mutableStateOf(Region.BERGEN) }

    // Hent lokasjon én gang og sett region automatisk
    LaunchedEffect(Unit) {
        // Bruk coroutine for å håndtere lokasjonsinnhenting asynkront
        activity?.let {
            val locationService = LocationService(it)
            try {
                val location = locationService.getCurrentLocation()
                location?.let {
                    selectedRegion = mapLocationToRegion(it)
                }
            } catch (e: Exception) {
                Log.e("PriceScreen", "Feil ved henting av lokasjon", e)
            }
        }
    }

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
                onAppearanceClicked = { showAppearance = true },
                navController = navController
            )
        }
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
                    ElectricityPriceChart(prices = prices)
                    Spacer(modifier = Modifier.height(16.dp))
                    PriceCard(prices)
                }
            }

            HelpBottomSheet(visible = showHelp, onDismiss = { showHelp = false })
            AppearanceBottomSheet(visible = showAppearance, onDismiss = { showAppearance = false }, fontScaleViewModel = fontScaleViewModel)
        }
    }
}

fun mapLocationToRegion(location: Location): Region {
    val lat = location.latitude
    val lon = location.longitude

    return when {
        // Østlandet / Oslo (NO1)
        lat in 59.5..61.5 && lon in 9.0..12.5 -> Region.OSLO
        // Sørlandet / Kristiansand (NO2)
        lat in 57.5..59.5 && lon in 6.0..9.5 -> Region.KRISTIANSAND
        // Midt-Norge / Trondheim (NO3)
        lat in 62.0..64.5 && lon in 9.0..12.0 -> Region.TRONDHEIM
        // Nord-Norge / Tromsø (NO4)
        lat in 68.0..70.5 && lon in 17.0..20.5 -> Region.TROMSO
        // Vestlandet / Bergen (NO5)
        lat in 60.0..61.5 && lon in 4.5..6.5 -> Region.BERGEN
        // Fallback hvis vi ikke finner match
        else -> Region.OSLO
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
            label = { Text("Velg distrikt", color = MaterialTheme.colorScheme.tertiary) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            textStyle = TextStyle(color = MaterialTheme.colorScheme.tertiary, fontSize = 18.sp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
                unfocusedBorderColor = MaterialTheme.colorScheme.tertiary
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Region.entries.forEach { region ->
                DropdownMenuItem(
                    text = { Text(region.displayName, color = MaterialTheme.colorScheme.tertiary) },
                    onClick = {
                        onRegionSelected(region)
                        expanded = false
                    },
                    colors = MenuItemColors(
                        leadingIconColor = MaterialTheme.colorScheme.secondary,
                        trailingIconColor = MaterialTheme.colorScheme.tertiary,
                        textColor = MaterialTheme.colorScheme.tertiary,
                        disabledTextColor = MaterialTheme.colorScheme.tertiary,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.primary,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        }
    }
}