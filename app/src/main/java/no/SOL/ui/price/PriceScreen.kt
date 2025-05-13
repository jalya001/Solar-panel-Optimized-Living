package no.SOL.ui.price

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import no.SOL.R
import no.SOL.model.price.Region
import no.SOL.model.price.getRegionName
import no.SOL.model.reusables.UiState
import no.SOL.ui.handling.ErrorScreen
import no.SOL.ui.handling.LoadingScreen

import no.SOL.util.RequestLocationPermission
import java.time.ZoneId
import java.time.ZonedDateTime


@Composable
fun PriceScreen(
    contentPadding: PaddingValues,
    viewModel: PriceViewModel = viewModel(),
) {
    val scrollState = rememberScrollState()
    val selectedRegion by viewModel.region.stateFlow.collectAsState()
    val prices by viewModel.prices.stateFlow.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.doFetchPrices()
    }

    // Request location and set region once on permission
    RequestLocationPermission { newRegion ->
        viewModel.setRegion(newRegion)
    }

    // Call ViewModel when selectedRegion changes
    LaunchedEffect(selectedRegion) {
        selectedRegion.let { viewModel.setRegion(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RegionDropdown(selectedRegion) { newRegion ->
            viewModel.setRegion(newRegion)
        }

        Spacer(modifier = Modifier.height(8.dp))

        val priceUiState by viewModel.priceUiState.collectAsStateWithLifecycle()

        when (priceUiState) {
            UiState.LOADING -> LoadingScreen()
            UiState.ERROR -> ErrorScreen()
            UiState.SUCCESS -> {
                Spacer(modifier = Modifier.height(24.dp))

                ElectricityPriceChart(prices = prices[selectedRegion]!!.data)
                Spacer(modifier = Modifier.height(16.dp))
                val currentHour = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).hour
                val initialIndex =
                    prices[selectedRegion]!!.data.indexOfFirst { ZonedDateTime.parse(it.time_start).hour == currentHour }
                var hourIndex by remember { mutableIntStateOf(initialIndex.coerceAtLeast(0)) }
                PriceCard(
                    prices = prices[selectedRegion]!!.data,
                    hourIndex = hourIndex,
                    onHourChange = { newIndex -> hourIndex = newIndex }
                )
            }
        }
    }
}

// Dropdown to select region in case user does not grant location permission - or just is curious
// about electricity prices in Norway
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