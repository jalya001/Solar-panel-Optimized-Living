package no.solcellepanelerApp.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import no.solcellepanelerApp.MainActivity
import no.solcellepanelerApp.R
import no.solcellepanelerApp.model.electricity.Region
import no.solcellepanelerApp.ui.electricity.HomePriceCard
import no.solcellepanelerApp.ui.electricity.PriceScreenViewModel
import no.solcellepanelerApp.ui.electricity.PriceUiState
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.handling.ErrorScreen
import no.solcellepanelerApp.ui.handling.LoadingScreen
import no.solcellepanelerApp.ui.navigation.AppearanceBottomSheet
import no.solcellepanelerApp.ui.navigation.BottomBar
import no.solcellepanelerApp.ui.navigation.HelpBottomSheet
import no.solcellepanelerApp.ui.onboarding.OnboardingUtils
import no.solcellepanelerApp.ui.result.WeatherViewModel
import no.solcellepanelerApp.ui.reusables.MyDisplayCard
import no.solcellepanelerApp.ui.reusables.MyNavCard
import no.solcellepanelerApp.ui.reusables.SimpleTutorialOverlay
import no.solcellepanelerApp.ui.theme.ThemeMode
import no.solcellepanelerApp.ui.theme.ThemeState
import no.solcellepanelerApp.util.fetchCoordinates
import no.solcellepanelerApp.util.mapLocationToRegion
import java.time.LocalTime
import java.time.ZonedDateTime


@SuppressLint("DefaultLocale")
@Composable
fun HomeScreen(
    navController: NavController,
    fontScaleViewModel: FontScaleViewModel,
    weatherViewModel: WeatherViewModel,
    priceScreenViewModel: PriceScreenViewModel,
) {
    val context = LocalContext.current
    val activity = (context as? MainActivity)
    val radiationArray by weatherViewModel.frostDataRim.collectAsState()

    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }
    val isLoading by remember { mutableStateOf(false) }

    var selectedRegion by rememberSaveable { mutableStateOf<Region?>(null) }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    Log.d("HomeScreen", "currentLocation: $currentLocation")
    var dataFetched by remember { mutableStateOf(false) }

    val onboardingUtils = remember { OnboardingUtils(context) }

    var showOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!onboardingUtils.isHomeOverlayShown()) {
            showOverlay = true
            onboardingUtils.setHomeOverlayShown()
        }
    }

    val currentHour by remember { mutableIntStateOf(ZonedDateTime.now().minusHours(2).hour) }
    //var currentHourValue by remember { mutableStateOf(radiationArray[currentHour]) }
    // The value for the current hour
    Log.e("HomeScreen", "currentHour: $currentHour")
    Log.e("HomeScreen", "radiationArray: ${radiationArray.joinToString(", ")}")
    var currentHourValueny by remember { mutableStateOf<Double?>(null) }
    Log.e("HomeScreen", "currentHourValueny: $currentHourValueny")
    LaunchedEffect(currentHour, radiationArray) {
        radiationArray.let {
            if (it.isNotEmpty()) {
                it[currentHour].let { currentHourValue ->
                    Log.e("HomeScreen", "currentHourValue: $currentHourValue")
                    currentHourValueny = currentHourValue / 1000.0
                }
            } else {
                Log.e("HomeScreen", "radiationArray is empty.")
            }
        }
    }

    if (isLoading) {
        LoadingScreen()
        return
    }

//    //Request location permission and fetch region
//    RequestLocationPermission { region ->
//        selectedRegion = region
//        locationPermissionGranted = true
//
//    }
//
//    LaunchedEffect(locationPermissionGranted) {
//        if (locationPermissionGranted && activity != null) {
//            val location = fetchCoordinates(context, activity)
//            currentLocation = location
//
//        }
//    }

    LaunchedEffect(Unit) {
        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted && activity != null) {
            val location = fetchCoordinates(activity)
            currentLocation = location
            selectedRegion = location?.let { mapLocationToRegion(it) } ?: Region.OSLO
        } else {
            selectedRegion = Region.OSLO
        }
    }


    Log.d("HomeScreen", "currentLocation: $currentLocation")
    LaunchedEffect(currentLocation) {
        if (currentLocation != null && !dataFetched) {
            Log.e(
                "HomeScreen",
                "currentLocation is now not null and is: ${currentLocation!!.latitude}, ${currentLocation!!.longitude}"
            )
            weatherViewModel.fetchRimData(
                currentLocation!!.latitude,
                currentLocation!!.longitude,
                "mean(surface_downwelling_shortwave_flux_in_air PT1H)"
            )
            dataFetched = true
            Log.d("HomeScreen", "radiationArray: $radiationArray")
        }
    }


    val isDark = when (ThemeState.themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }


    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .padding(top = 35.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
//                        .background(Color.Red)
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(
                            id = if (isDark) R.drawable.logo_topbar_dark else R.drawable.logo_topbar_light
                        ),
                        contentDescription = "",
                        modifier = Modifier
                            .height(100.dp)
                    )
                }
            }
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
//                .background(Color.Blue)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (showOverlay) {
                SimpleTutorialOverlay(
                    onDismiss = { showOverlay = false },
                    stringResource(R.string.home_overlay)
                )
            }

            MyNavCard(
                text = stringResource(id = R.string.install_panels_title),
                desc = stringResource(id = R.string.install_panels_desc),
                route = "map",
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                style = MaterialTheme.typography.displaySmall,
//                content = { PanelAnimation() },
                color = MaterialTheme.colorScheme.tertiary
//                color = MaterialTheme.colorScheme.primary
//                color = MaterialTheme.colorScheme.secondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {

                MyDisplayCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(400.dp),
                    style = MaterialTheme.typography.displaySmall,
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    3.dp,
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(15.dp)
                                )
//                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(start = 10.dp, end = 10.dp, top = 20.dp),

                            horizontalAlignment = Alignment.CenterHorizontally // Center text horizontally
                        ) {
                            val timenow = LocalTime.now().hour
                            Text(
                                text = stringResource(R.string.live_energy, timenow),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.secondary
//                                color = MaterialTheme.colorScheme.tertiary Oransje fargen. bare å fjerne kommentaren her hvis dere vil bruke oransj d
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            if (currentHourValueny != null) {
                                Text(
                                    text = currentHourValueny?.let {
                                        String.format(
                                            "%.4f",
                                            it
                                        ) + " kW/m²"
                                    } ?: "No data",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraLight,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                SunAnimation(currentHourValueny ?: 0.0)
                            } else {
                                LoadingScreen()
                            }

                        }
                    },
                    color = MaterialTheme.colorScheme.primary
                )

                MyNavCard(
                    route = "prices",
                    navController = navController,
                    modifier = Modifier
                        .weight(1f)
                        .height(400.dp),
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
                                    text = stringResource(R.string.live_prices),
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.secondary
//                                color = MaterialTheme.colorScheme.tertiary Oransje fargen. bare å fjerne kommentaren her hvis dere vil bruke oransj d
                                )
                                Spacer(modifier = Modifier.height(10.dp))


                                // Show loading screen until the region is selected
                                if (selectedRegion == null) {
                                    LoadingScreen()
                                } else {


                                    val priceUiState by priceScreenViewModel.priceUiState.collectAsStateWithLifecycle()

                                    when (priceUiState) {
                                        is PriceUiState.Loading -> LoadingScreen()
                                        is PriceUiState.Error -> ErrorScreen()
                                        is PriceUiState.Success -> {
                                            val prices =
                                                (priceUiState as PriceUiState.Success).prices
                                            Column {
                                                HomePriceCard(prices, selectedRegion)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    color = MaterialTheme.colorScheme.primary
                )

            }

            HelpBottomSheet(
                navController = navController,
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

//@Composable
//fun PanelAnimation() {
//    val animationFile = "solarPanel_anim.json"
//
//    val composition by rememberLottieComposition(
//        LottieCompositionSpec.Asset(animationFile)
//    )
//
//    val progress by animateLottieCompositionAsState(
//        composition = composition,
//        iterations = LottieConstants.IterateForever,
//    )
//
//    Box(
//        modifier = Modifier
//            .height(100.dp)
//    ) {
//        LottieAnimation(
//            composition = composition,
//            progress = { progress },
//            modifier = Modifier
//                .width(130.dp)
//                .aspectRatio(400.dp / 1000.dp),
//        )
//    }
//}

@Composable
fun ElectricityTowers() {

    val isDark = when (ThemeState.themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }


    val animationFile =
        if (isDark) "electricity_tower_dark.json" else "electricity_tower_light.json"

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(animationFile)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    Box(
        modifier = Modifier
//            .background(Color.Blue)
        ,
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .width(150.dp)
                .aspectRatio(400.dp / 400.dp),
//                .background(Color.Red)
        )
    }
}

@Composable
fun SunAnimation(value: Double) {
    val animationFile = when {
        value < 0.03 -> "solar_verylow.json"
        value in 0.03..0.1 -> "solar_low.json"
        value in 0.1..0.3 -> "solar_half.json"
        value > 0.3 -> "solar_full.json"
        else -> "solar_verylow.json" // Default animation
    }

    // Force new composition when value changes
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.Asset(animationFile),
    )

    // Reset animation state when value changes
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        // Add a key to restart animation when value changes
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .height(450.dp)
            .fillMaxWidth()
            .graphicsLayer(
                scaleX = 1.8f,
                scaleY = 1.8f
            )
    )
    Log.d("SunAnimation", "Animating with value: $value")
}

@Preview(showBackground = true)
@Composable
fun HomeOverlayPreview() {
    SimpleTutorialOverlay(
        onDismiss = {},
        message = "Welcome to your home screen!\n\n" +
                "Tap \"Find your solar potential\" to see how much you can save with solar panels!\n\n" +
                "Tap the electricity prices in the bottom right corner to get more details about energy costs.\n\n"
    )
}
