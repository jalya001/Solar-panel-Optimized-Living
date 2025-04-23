package no.solcellepanelerApp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import no.solcellepanelerApp.R
import no.solcellepanelerApp.data.homedata.ElectricityPriceRepository
import no.solcellepanelerApp.model.electricity.Region
import no.solcellepanelerApp.ui.electricity.PriceCard
import no.solcellepanelerApp.ui.electricity.PriceScreenViewModel
import no.solcellepanelerApp.ui.electricity.PriceUiState
import no.solcellepanelerApp.ui.electricity.PriceViewModelFactory
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.handling.ErrorScreen
import no.solcellepanelerApp.ui.handling.LoadingScreen
import no.solcellepanelerApp.ui.navigation.AppearanceBottomSheet
import no.solcellepanelerApp.ui.navigation.BottomBar
import no.solcellepanelerApp.ui.navigation.HelpBottomSheet
import no.solcellepanelerApp.ui.navigation.TopBar
import no.solcellepanelerApp.ui.reusables.MyNavCard
import no.solcellepanelerApp.util.RequestLocationPermission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    fontScaleViewModel: FontScaleViewModel,
) {
    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var selectedRegion by rememberSaveable { mutableStateOf<Region?>(null) }

    if (isLoading) {
        LoadingScreen()
        return
    }

    //Request location permission and fetch region
    RequestLocationPermission { region ->
        selectedRegion = region
    }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                text = "*IKON og APPNAVN*",
                backClick = false,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            MyNavCard(
                text = stringResource(id = R.string.install_panels),
                route = "map",
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                style = MaterialTheme.typography.displayLarge,
                content = { PanelAnimation() },
                color = MaterialTheme.colorScheme.tertiary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                MyNavCard(
                    text = stringResource(id = R.string.last_location),
                    route = "last_location",
                    navController = navController,
                    modifier = Modifier
                        .weight(1f)
                        .height(400.dp),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                MyNavCard(
                    //text = stringResource(id = R.string.prices),
                    route = "prices",
                    navController = navController,
                    modifier = Modifier
                        .weight(1f)
                        .height(400.dp),
                    style = MaterialTheme.typography.displaySmall,
                    //content = { LightningAnimation() },
                    content = {
                        Column {
                            Text(text = "Viser nå strømpriser for $selectedRegion:")

                            Spacer(modifier = Modifier.padding(8.dp))
                            // Show loading screen until the region is selected
                            if (selectedRegion == null) {
                                LoadingScreen()
                            } else {
                                val repository =
                                    ElectricityPriceRepository(priceArea = selectedRegion!!.regionCode)

                                val viewModel: PriceScreenViewModel = viewModel(
                                    factory = PriceViewModelFactory(
                                        repository,
                                        selectedRegion!!.regionCode
                                    ),
                                    key = selectedRegion!!.regionCode
                                )

                                val priceUiState by viewModel.priceUiState.collectAsStateWithLifecycle()

                                when (priceUiState) {
                                    is PriceUiState.Loading -> LoadingScreen()
                                    is PriceUiState.Error -> ErrorScreen()
                                    is PriceUiState.Success -> {
                                        val prices = (priceUiState as PriceUiState.Success).prices
                                        PriceCard(prices)
                                    }
                                }
                            }
                        }
                    },
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HelpBottomSheet(
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
}

@Composable
fun PanelAnimation() {
    val animationFile = "solarPanel_anim.json"

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(animationFile)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    Box(
        modifier = Modifier
            .height(100.dp)
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .width(130.dp)
                .aspectRatio(400.dp / 1000.dp),
        )
    }
}

@Composable
fun LightningAnimation() {
    val animationFile = "lightningBolt_anim.json"

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(animationFile)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    Box(
        modifier = Modifier
//        .background(color = Color.Blue)
            .height(500.dp)
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-40).dp, y = (40).dp)
        )
    }
}

