package no.solcellepanelerApp.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import no.solcellepanelerApp.ui.electricity.HomePriceCard
import no.solcellepanelerApp.ui.electricity.PriceScreenViewModel
import no.solcellepanelerApp.ui.electricity.PriceUiState
import no.solcellepanelerApp.ui.electricity.PriceViewModelFactory
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.handling.ErrorScreen
import no.solcellepanelerApp.ui.handling.LoadingScreen
import no.solcellepanelerApp.ui.navigation.AppearanceBottomSheet
import no.solcellepanelerApp.ui.navigation.BottomBar
import no.solcellepanelerApp.ui.navigation.HelpBottomSheet
import no.solcellepanelerApp.ui.reusables.MyDisplayCard
import no.solcellepanelerApp.ui.reusables.MyNavCard
import no.solcellepanelerApp.ui.theme.ThemeMode
import no.solcellepanelerApp.ui.theme.ThemeState
import java.time.ZonedDateTime

@Composable
private fun isDarkThemeEnabled(): Boolean = when (ThemeState.themeMode) {
    ThemeMode.DARK -> true
    ThemeMode.LIGHT -> false
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
}

@Composable
fun HomeScreen(
    navController: NavController,
    fontScaleViewModel: FontScaleViewModel,
    homeScreenViewModel: HomeScreenViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        homeScreenViewModel.initialize(context)
    }

    val isLoading by homeScreenViewModel.isLoading.collectAsState()
    val currentRadiationValue by homeScreenViewModel.currentRadiationValue.collectAsState()
    val selectedRegion by homeScreenViewModel.selectedRegion.collectAsState()
    val currentTime by homeScreenViewModel.currentTime.collectAsState()
    val showHelp by homeScreenViewModel.showHelp.collectAsState()
    val showAppearance by homeScreenViewModel.showAppearance.collectAsState()

    if (isLoading) {
        LoadingScreen()
        return
    }
    val isDarkTheme = isDarkThemeEnabled()


    Scaffold(
        topBar = { TopBar(isDarkTheme = isDarkTheme) },
        bottomBar = {
            BottomBar(
                onHelpClicked = { homeScreenViewModel.setShowHelp(true) },
                onAppearanceClicked = { homeScreenViewModel.setShowAppearance(true) },
                navController = navController,
            )
        },
    ) { contentPadding ->
        MainContent(
            contentPadding = contentPadding,
            navController = navController,
            currentRadiationValue = currentRadiationValue,
            selectedRegion = selectedRegion,
            currentTime = currentTime,
        )

        HelpBottomSheet(
            navController = navController,
            visible = showHelp,
            onDismiss = { homeScreenViewModel.setShowHelp(false) },
        )

        AppearanceBottomSheet(
            visible = showAppearance,
            onDismiss = { homeScreenViewModel.setShowAppearance(false) },
            fontScaleViewModel = fontScaleViewModel,
        )
    }
}

@Composable
fun MainContent(
    contentPadding: PaddingValues,
    navController: NavController,
    currentRadiationValue: Double?,
    selectedRegion: Region?,
    currentTime: ZonedDateTime,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
            //.background(Color.Blue),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SolarPanelInstallationCard(navController)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            CurrentRadiationCard(currentRadiationValue, Modifier.weight(1f), currentTime)
            ElectricityPriceCard(selectedRegion, navController, Modifier.weight(1f))
        }
    }
}

@Composable
fun TopBar(isDarkTheme: Boolean) {
    Surface(
        modifier = Modifier.padding(top = 35.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
//              .background(Color.Red)
            ,
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(
                    id = if (isDarkTheme) R.drawable.logo_topbar_dark else R.drawable.logo_topbar_light
                ),
                contentDescription = "",
                modifier = Modifier
                    .height(100.dp)
            )
        }
    }
}

@Composable
fun SolarPanelInstallationCard(navController: NavController) {
    MyNavCard(
        text = stringResource(id = R.string.install_panels_title),
        desc = stringResource(id = R.string.install_panels_desc),
        route = "map",
        navController = navController,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        style = MaterialTheme.typography.displaySmall,
//      content = { PanelAnimation() },
        color = MaterialTheme.colorScheme.tertiary
//      color = MaterialTheme.colorScheme.primary
//      color = MaterialTheme.colorScheme.secondary
    )
}

@SuppressLint("DefaultLocale")
@Composable
fun CurrentRadiationCard(
    currentRadiationValue: Double?,
    modifier: Modifier = Modifier,
    currentTime: ZonedDateTime
) {
    MyDisplayCard(
        modifier = modifier.height(400.dp),
        style = MaterialTheme.typography.displaySmall,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(15.dp))
                    .padding(start = 10.dp, end = 10.dp, top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "LIVE ENERGY ${currentTime.hour}:00 ",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary
//                  color = MaterialTheme.colorScheme.tertiary Oransje fargen. bare å fjerne kommentaren her hvis dere vil bruke oransj d
                )
                Spacer(modifier = Modifier.height(10.dp))
                if (currentRadiationValue != null) {
                    Text(
                        text = currentRadiationValue?.let {
                            String.format(
                                "%.4f",
                                it
                            ) + " kW/m²"
                        } ?: "No data", // Why do we have this if it can't be null to begin with?
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraLight,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    SunAnimation(currentRadiationValue ?: 0.0) // But it can't be null?
                } else {
                    LoadingScreen()
                }
            }
        },
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
fun ElectricityPriceCard(
    selectedRegion: Region?,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    MyNavCard(
        route = "prices",
        navController = navController,
        modifier = modifier.height(400.dp),
        style = MaterialTheme.typography.headlineSmall,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, end = 10.dp, top = 20.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Se strømprisene!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary
//                      color = MaterialTheme.colorScheme.tertiary Oransje fargen. bare å fjerne kommentaren her hvis dere vil bruke oransj d
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (selectedRegion == null) {
                        LoadingScreen()
                    } else {
                        PriceInfo(selectedRegion = selectedRegion)
                    }
                }
            }
        },
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
fun PriceInfo(selectedRegion: Region) {
    /* MOVE THIS TO VIEWMODEL */
    val repository = ElectricityPriceRepository(priceArea = selectedRegion.regionCode)
    val viewModel: PriceScreenViewModel = viewModel(
        factory = PriceViewModelFactory(
            repository,
            selectedRegion.regionCode,
        ),
        key = selectedRegion.regionCode,
    )

    val priceUiState by viewModel.priceUiState.collectAsStateWithLifecycle()

    when (priceUiState) {
        is PriceUiState.Loading -> LoadingScreen()
        is PriceUiState.Error -> ErrorScreen()
        is PriceUiState.Success -> {
            val prices = (priceUiState as PriceUiState.Success).prices
            Column {
                HomePriceCard(prices, selectedRegion)
            }
        }
    }
}


@Composable
fun IndefiniteAnimationBox(
    animationFile: String,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(animationFile))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    Box(
        modifier = modifier,
        contentAlignment = contentAlignment
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = innerModifier
        )
    }
}

@Composable
fun PanelAnimation() {
    IndefiniteAnimationBox(
        animationFile = "solarPanel_anim.json",
        modifier = Modifier
            .height(100.dp),
        innerModifier = Modifier
            .width(130.dp)
            .aspectRatio(400.dp / 1000.dp)
    )
}

@Composable
fun ElectricityTowers() {
    val animationFile =
        if (isDarkThemeEnabled()) "electricity_tower_dark.json" else "electricity_tower_light.json"
    IndefiniteAnimationBox(
        animationFile = animationFile,
        innerModifier = Modifier
            .width(150.dp)
            .aspectRatio(400.dp / 400.dp),
        contentAlignment = Alignment.Center
    )
}

@Composable
fun LightningAnimation() {
    IndefiniteAnimationBox(
        animationFile = "lightningBolt_anim.json",
        modifier = Modifier
            .height(500.dp),
        innerModifier = Modifier
            .size(150.dp)
    )
}

@Composable
fun SunAnimation(radiationValue: Double) {
    val animationFile = when {
        radiationValue < 0.03 -> "solar_verylow.json"
        radiationValue in 0.03..0.1 -> "solar_low.json"
        radiationValue in 0.1..0.3 -> "solar_half.json"
        radiationValue > 0.3 -> "solar_full.json"
        else -> "solar_verylow.json" // Default animation
    }
    IndefiniteAnimationBox(
        animationFile = animationFile,
        innerModifier = Modifier
            .height(450.dp)
            .fillMaxWidth()
            .graphicsLayer(scaleX = 1.8f, scaleY = 1.8f)
    )
    Log.d("SunAnimation", "Animating with value: $radiationValue")
}