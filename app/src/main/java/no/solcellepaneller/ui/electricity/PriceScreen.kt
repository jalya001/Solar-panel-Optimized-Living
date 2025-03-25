package no.solcellepaneller.ui.electricity

import androidx.compose.foundation.background
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import no.solcellepaneller.data.homedata.ElectricityPriceRepository
import no.solcellepaneller.model.electricity.ElectricityPrice
import no.solcellepaneller.model.electricity.Region


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceScreen(
    onBackClick: () -> Unit,
    repository: ElectricityPriceRepository,
    isDarkMode: Boolean
) {
    var selectedRegion by remember { mutableStateOf(Region.OSLO) }
    val viewModel: PriceScreenViewModel = viewModel(
        factory = PriceViewModelFactory(repository, selectedRegion.regionCode)
    )

    val priceUiState by viewModel.priceUiState.collectAsStateWithLifecycle()

    val backgroundColor = if (isDarkMode) Color(0xFF0C1618) else Color(0xFFC3DFE0)
    val textColor = if (isDarkMode) Color(0xFFF3A712) else Color(0xFF0C1618)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Strømpriser", color = textColor) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Hjem", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundColor),
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
                    PriceList(prices, textColor)
                }
            }
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
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFC3DFE0),
                unfocusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color(0xFFE0E0E0),
                focusedIndicatorColor = Color.Blue
            ),
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
                    },
                    modifier = Modifier.background(Color(0xFFC3DFE0))
                )
            }
        }
    }
}

@Composable
fun PriceList(prices: List<ElectricityPrice>, textColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        prices.forEach { price ->
            Text(
                text = "Pris: ${price.NOK_per_kWh} NOK/kWh",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
            Text(
                text = "Tid: ${price.getTimeRange()}",
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
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
