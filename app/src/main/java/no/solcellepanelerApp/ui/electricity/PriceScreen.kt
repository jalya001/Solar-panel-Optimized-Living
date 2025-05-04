package no.solcellepanelerApp.ui.electricity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.solcellepanelerApp.R
import no.solcellepanelerApp.data.homedata.ElectricityPriceRepository
import no.solcellepanelerApp.model.electricity.Region
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.handling.ErrorScreen
import no.solcellepanelerApp.ui.handling.LoadingScreen
import no.solcellepanelerApp.ui.navigation.AppearanceBottomSheet
import no.solcellepanelerApp.ui.navigation.BottomBar
import no.solcellepanelerApp.ui.navigation.HelpBottomSheet
import no.solcellepanelerApp.ui.navigation.TopBar
import no.solcellepanelerApp.util.RequestLocationPermission
import java.time.ZoneId
import java.time.ZonedDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceScreen(
    repository: ElectricityPriceRepository,
    navController: NavController,
    fontScaleViewModel: FontScaleViewModel,
) {
    val scrollState = rememberScrollState()

    var selectedRegion by remember { mutableStateOf<Region?>(null) }

    RequestLocationPermission { newRegion ->
        selectedRegion = newRegion
    }

    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar(navController, stringResource(id = R.string.price_title)) },
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
                .verticalScroll(scrollState)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            selectedRegion?.let {
                RegionDropdown(it) { newRegion ->
                    selectedRegion = newRegion
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedRegion != null) {
                val viewModel: PriceScreenViewModel = viewModel(
                    factory = PriceViewModelFactory(repository, selectedRegion!!.regionCode),
                    key = selectedRegion!!.regionCode
                )

                val priceUiState by viewModel.priceUiState.collectAsStateWithLifecycle()

                //De her fyller ikke hele boksen av en eller annen grunn
                when (priceUiState) {
                    is PriceUiState.Loading ->
                        LoadingScreen()


                    is PriceUiState.Error ->
                        ErrorScreen()


                    is PriceUiState.Success -> {
                        val prices = (priceUiState as PriceUiState.Success).prices
                        ElectricityPriceChart(prices = prices)
                        Spacer(modifier = Modifier.height(16.dp))
                        val currentHour = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).hour
                        val initialIndex =
                            prices.indexOfFirst { ZonedDateTime.parse(it.time_start).hour == currentHour }
                        var hourIndex by remember { mutableStateOf(initialIndex.coerceAtLeast(0)) }
                        PriceCard(
                            prices = prices,
                            hourIndex = hourIndex,
                            onHourChange = { newIndex -> hourIndex = newIndex }
                        )
                    }
                }
            } else {
                LoadingScreen()
            }

            HelpBottomSheet(
                navController = navController,
                visible = showHelp,
                onDismiss = { showHelp = false })
            AppearanceBottomSheet(
                visible = showAppearance,
                onDismiss = { showAppearance = false },
                fontScaleViewModel = fontScaleViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionDropdown(
    selectedRegion: Region,
    onRegionSelected: (Region) -> Unit,
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
//            colors = OutlinedTextFieldDefaults.colors(
//                unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
//                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
//                focusedContainerColor = MaterialTheme.colorScheme.background,
//                focusedLabelColor = MaterialTheme.colorScheme.tertiary,
//                unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
//                unfocusedBorderColor = MaterialTheme.colorScheme.tertiary
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
//                    colors = MenuItemColors(
//                        leadingIconColor = MaterialTheme.colorScheme.secondary,
//                        trailingIconColor = MaterialTheme.colorScheme.tertiary,
//                        textColor = MaterialTheme.colorScheme.tertiary,
//                        disabledTextColor = MaterialTheme.colorScheme.tertiary,
//                        disabledLeadingIconColor = MaterialTheme.colorScheme.primary,
//                        disabledTrailingIconColor = MaterialTheme.colorScheme.tertiary
//                    )
                )
            }
        }
    }
}