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
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.navigation.NavController
import no.solcellepanelerApp.R
import no.solcellepanelerApp.model.electricity.Region
import no.solcellepanelerApp.model.electricity.getRegionName
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


@Composable
fun PriceScreen(
    viewModel: PriceScreenViewModel,
    navController: NavController,
    fontScaleViewModel: FontScaleViewModel,
) {
    val scrollState = rememberScrollState()

    var selectedRegion by remember { mutableStateOf<Region?>(null) }

    // Request location and set region once on permission
    RequestLocationPermission { newRegion ->
        selectedRegion = newRegion
    }

    // Call ViewModel when selectedRegion changes
    LaunchedEffect(selectedRegion) {
        selectedRegion?.let { viewModel.setRegion(it) }
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

            val priceUiState by viewModel.priceUiState.collectAsStateWithLifecycle()

            when (priceUiState) {
                is PriceUiState.Loading -> {
                    Spacer(modifier = Modifier.height(260.dp))
                    LoadingScreen()
                }
                is PriceUiState.Error -> {
                    Spacer(modifier = Modifier.height(260.dp))
                    ErrorScreen()
                }
                is PriceUiState.Success -> {
                    val prices = (priceUiState as PriceUiState.Success).prices
                    Spacer(modifier = Modifier.height(24.dp))
                    ElectricityPriceChart(prices = prices)
                    Spacer(modifier = Modifier.height(16.dp))
                    val currentHour = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).hour
                    val initialIndex =
                        prices.indexOfFirst { ZonedDateTime.parse(it.time_start).hour == currentHour }
                    var hourIndex by remember { mutableIntStateOf(initialIndex.coerceAtLeast(0)) }
                    PriceCard(
                        prices = prices,
                        hourIndex = hourIndex,
                        onHourChange = { newIndex -> hourIndex = newIndex }
                    )
                }
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
    onRegionSelected: (Region) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = getRegionName(selectedRegion),
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    stringResource(R.string.region),
                    color = MaterialTheme.colorScheme.tertiary
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            textStyle = TextStyle(color = MaterialTheme.colorScheme.tertiary, fontSize = 18.sp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    MenuAnchorType.PrimaryNotEditable,
                    true
                ), // Updated to use non-deprecated version
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Region.entries.forEach { region ->
                DropdownMenuItem(
                    text = {
                        Text(
                            getRegionName(region),
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        onRegionSelected(region)
                        expanded = false
                    }
                )
            }
        }
    }
}