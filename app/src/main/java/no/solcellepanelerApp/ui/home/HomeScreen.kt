package no.solcellepanelerApp.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import no.solcellepanelerApp.R
import no.solcellepanelerApp.model.reusables.UiState
import no.solcellepanelerApp.ui.price.HomePriceCard
import no.solcellepanelerApp.ui.handling.ErrorScreen
import no.solcellepanelerApp.ui.handling.LoadingScreen
import no.solcellepanelerApp.ui.reusables.MyDisplayCard
import no.solcellepanelerApp.ui.reusables.MyNavCard
import no.solcellepanelerApp.ui.theme.isDarkThemeEnabled
import java.time.ZonedDateTime

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(),
    contentPadding: PaddingValues
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        homeViewModel.initialize(context)
    }

    val isLoading by homeViewModel.isLoading.collectAsState()
    val currentTime by homeViewModel.currentTime.collectAsState()

    if (isLoading) {
        LoadingScreen()
        return
    }

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
            CurrentRadiationCard(homeViewModel, Modifier.weight(1f), currentTime)
            ElectricityPriceCard(
                homeViewModel,
                navController,
                Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun HomeTopBar(isDarkTheme: Boolean) {
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
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    currentTime: ZonedDateTime
) {
    val currentRadiationValue by homeViewModel.currentRadiationValue.collectAsState()
    val rimUiState by homeViewModel.rimUiState.collectAsState()

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
                when (rimUiState) {
                    UiState.LOADING -> { LoadingScreen() }
                    UiState.SUCCESS -> {
                        Text(
                            text = String.format("%.4f", currentRadiationValue) + " kW/m²",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraLight,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        SunAnimation(currentRadiationValue!!)
                    }
                    else -> { ErrorScreen() }
                }
            }
        },
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
fun ElectricityPriceCard(
    homeViewModel: HomeViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
) {

    val selectedRegion by homeViewModel.region.stateFlow.collectAsState()
    val priceUiState by homeViewModel.priceUiState.collectAsState()
    val prices = homeViewModel.prices.stateFlow.collectAsState()

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
                    when (priceUiState) {
                        UiState.LOADING -> LoadingScreen()
                        UiState.ERROR -> ErrorScreen()
                        UiState.SUCCESS -> {
                            Column {
                                HomePriceCard(prices.value[selectedRegion]?.data?: emptyList(), selectedRegion)
                            }
                        }
                    }
                }
            }
        },
        color = MaterialTheme.colorScheme.primary,
    )
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
/*
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
*/
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
/*
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
*/
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