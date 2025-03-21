package no.solcellepaneller.ui.electricity

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import no.solcellepaneller.data.homedata.ElectricityPriceRepository
import no.solcellepaneller.model.electricity.ElectricityPrice


@Composable
fun PriceScreen(
    region: String,
    onBackClick: () -> Unit,
    repository: ElectricityPriceRepository,
) {
    val viewModel: PriceScreenViewModel = viewModel(
        factory = PriceViewModelFactory(repository, region)
    )

    val priceUiState by viewModel.priceUiState.collectAsStateWithLifecycle()

    when (priceUiState) {
        is PriceUiState.Loading -> LoadingScreen()
        is PriceUiState.Error -> ErrorScreen()
        is PriceUiState.Success -> {
            val prices = (priceUiState as PriceUiState.Success).prices
            PriceDetailScreen(prices, onBackClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceDetailScreen(prices: List<ElectricityPrice>, onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Strømpriser") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Hjem")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Strømpriser for valgt region",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (prices.isNotEmpty()) {
                prices.forEach { price ->
                    PriceItem(price)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                Text("Ingen priser tilgjengelig", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun PriceItem(price: ElectricityPrice) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = "Pris: ${price.NOK_per_kWh} NOK/kWh",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Tid: ${price.getTimeRange()}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
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
