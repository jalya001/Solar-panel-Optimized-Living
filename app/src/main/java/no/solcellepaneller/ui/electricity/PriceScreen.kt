package no.solcellepaneller.ui.electricity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import no.solcellepaneller.data.homedata.ElectricityPriceRepository
import no.solcellepaneller.model.electricity.ElectricityPrice
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.HelpBottomSheet
import no.solcellepaneller.ui.navigation.InformationBottomSheet
import no.solcellepaneller.ui.navigation.TopBar
import no.solcellepaneller.ui.theme.SolcellepanellerTheme


@Composable
fun PriceScreen(
    region: String,
    repository: ElectricityPriceRepository,navController: NavController
) {
    val viewModel: PriceScreenViewModel = viewModel(
        factory = PriceViewModelFactory(repository, region)
    )

    val priceUiState by viewModel.priceUiState.collectAsStateWithLifecycle()

    when (priceUiState) {
        is PriceUiState.Loading -> LoadingScreen()
        is PriceUiState.Error -> ErrorScreen(navController)
        is PriceUiState.Success -> {
            val prices = (priceUiState as PriceUiState.Success).prices
            PriceDetailScreen(prices,navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceDetailScreen(prices: List<ElectricityPrice>,navController: NavController) {
    var showHelp by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Strømpriser") },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.Filled.ArrowBack, contentDescription = "Hjem")
//                    }
//                }
//            )
//        }
        topBar = { TopBar(navController) },
        bottomBar = {
            BottomBar(
                onHelpClicked = { showHelp = true },
                onInfoClicked = { showInfo = true },
                onAppearanceClicked = { showAppearance = true }) }
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

            HelpBottomSheet(visible = showHelp, onDismiss = { showHelp = false })
            InformationBottomSheet(visible = showInfo, onDismiss = { showInfo = false })
            AppearanceBottomSheet(visible = showAppearance, onDismiss = { showAppearance = false })
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
fun ErrorScreen(navController: NavController) {
    Scaffold(
        topBar = { TopBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Noe gikk galt! Prøv igjen senere.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PriceDetailScreenPreview() {
    val fakePrices = listOf(
        ElectricityPrice(
            NOK_per_kWh = 1.49,
            time_start = "10:00",
            time_end = "11:00",
            date = "2025-04-03",
            region = "NO1"
        ),
        ElectricityPrice(
            NOK_per_kWh = 0.99,
            time_start = "11:00",
            time_end = "12:00",
            date = "2025-04-03",
            region = "NO1"
        )
    )

    SolcellepanellerTheme {
        PriceDetailScreen(prices = fakePrices, navController = rememberNavController())
    }
}