package no.solcellepaneller.ui.result

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import no.solcellepaneller.R
import no.solcellepaneller.ui.font.FontScaleViewModel
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.HelpBottomSheet
import no.solcellepaneller.ui.navigation.TopBar


@Composable
fun ShowProduce(
    energy: Double,
    navController: NavController,
    fontScaleViewModel: FontScaleViewModel,
) {
    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }
    var currentEnergy by remember { mutableStateOf(energy) }

    val devices = listOf(
        "🧊 Fridge" to 30.0,
        "🧺 Washing Machine" to 20.0,
        "📺 TV" to 15.0,
        "💻 Laptop" to 10.0,
        "❄️ Air Conditioner" to 50.0,
        "🔥 Heater" to 60.0,
        "🍲 Microwave" to 25.0,
        "🍽️ Dishwasher" to 35.0,
        "🚗 El-Car" to 100.0,
        "🧹 Vacuum Cleaner" to 8.0
    )
    var connectedDevices by remember { mutableStateOf(mutableMapOf<String, Double>()) }

    // Animation for current energy value
    val animatedEnergy = animateFloatAsState(
        targetValue = currentEnergy.toFloat(),
        animationSpec = tween(durationMillis = 500, easing = LinearEasing)
    ).value

    // Animation for color based on energy change (red for down, green for up)
    val energyColor = animateColorAsState(
        targetValue = if (currentEnergy < 0) Color.Red else Color.Green,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing)
    ).value

    Scaffold(
        topBar = { TopBar(navController, text = stringResource(R.string.prod)) },
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
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (currentEnergy < 0) {
                Text(
                    text = "⚠️ Energy deficit!%.2f kWh".format(animatedEnergy),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                Text(
                    text = "🔋 %.2f kWh".format(animatedEnergy),
                    style = MaterialTheme.typography.titleLarge,
                    color = energyColor, // Set the animated color
                    modifier = Modifier.padding(16.dp)
                )
            }
            EnergyFlowAnimationDown()
            Spacer(Modifier.padding(17.dp))
            HouseAnimation()
            Spacer(Modifier.padding(17.dp))
            EnergyFlowDown()
            LazyRow(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(devices) { (name, value) ->
                    val connected = connectedDevices.containsKey(name)
                    val glowAlpha by animateFloatAsState(
                        targetValue = if (connected) 1f else 0f,
                        animationSpec = tween(durationMillis = 500)
                    )
                    Card(
                        modifier = Modifier
                            .width(150.dp)
                            .border(
                                width = if (connected) 3.dp else 1.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .shadow(
                                elevation = if (connected) 8.dp else 2.dp,
                                shape = RoundedCornerShape(12.dp),
                                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha)
                            )
                            .clickable {
                                if (connected) {
                                    // Device is connected, remove from connected devices and add energy back
                                    connectedDevices = connectedDevices.toMutableMap().apply {
                                        remove(name)
                                    }

                                    currentEnergy += value
                                } else {
                                    // Device is disconnected, add to connected devices and subtract energy
                                    connectedDevices = connectedDevices.toMutableMap().apply {
                                        put(name, value)
                                    }

                                    currentEnergy -= value

                                }

                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (connected) Color.Green else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "%.2f kWh".format(value),
                                fontWeight = if (connected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
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


@Composable
fun EnergyFlowAnimationDown() {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("energy_down.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
    )
}

@Composable
fun EnergyFlowAnimationUp() {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("energy_down.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .height(100.dp)
            .graphicsLayer(
                rotationZ = 180f // rotate 180 degrees
            )
    )

}

@Composable
fun HouseAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("house.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
    )
}

@Composable
fun EnergyFlowDown() {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("flow.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .height(100.dp)
            .graphicsLayer(
                rotationZ = 180f // rotate 180 degrees
            )
    )

}