package no.solcellepanelerApp.ui.result

//import androidx.compose.foundation.layout.FlowRow
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.flowlayout.FlowRow
import no.solcellepanelerApp.R
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.reusables.IconTextRow


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShowProduce(
    energy: Double,
    navController: NavController,
    fontScaleViewModel: FontScaleViewModel,
) {
    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }
    var currentEnergy by remember { mutableStateOf(energy) }

    //dette må oversettes
    val devices = listOf(
        "Tester" to 1000000.0,
        "El-Car" to 100.0,
        "Fridge" to 30.0,
        "Heater" to 60.0,
        "Laptop" to 10.0,
        "Washing Machine" to 20.0,
        "TV" to 15.0,
        "Air Conditioner" to 50.0,
        "Microwave" to 25.0,
        "Dishwasher" to 35.0,
        "Vacuum Cleaner" to 8.0
    )

    val deviceIcons = mapOf(
        "Fridge" to R.drawable.kitchen_24px,
        "Washing Machine" to R.drawable.local_laundry_service_24px,
        "TV" to R.drawable.tv_24px,
        "Laptop" to R.drawable.laptop_windows_24px,
        "Air Conditioner" to R.drawable.mode_fan_24px,
        "Heater" to R.drawable.fireplace_24px,
        "Microwave" to R.drawable.microwave_24px,
        "Dishwasher" to R.drawable.dishwasher_24px,
        "El-Car" to R.drawable.directions_car_24px,
        "Vacuum Cleaner" to R.drawable.vacuum_24px,
    )

    var connectedDevices by remember { mutableStateOf(mutableMapOf<String, Double>()) }

    // Animation for current energy value
    val animatedEnergy = animateFloatAsState(
        targetValue = currentEnergy.toFloat(),
        animationSpec = tween(durationMillis = 500, easing = LinearEasing)
    ).value

    // Animation for color based on energy change (red for down, green for up)
    val energyColor = animateColorAsState(
        targetValue = if (currentEnergy < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
//        targetValue = if (currentEnergy < 0) Color.Red else Color.Green
        ,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing)
    ).value

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (currentEnergy < 0) {
            IconTextRow(
                iconRes = R.drawable.baseline_battery_charging_full_24,
                text = "Energy deficit! %.2f kWh".format(animatedEnergy),
                textStyle = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp),
                textColor = MaterialTheme.colorScheme.error,
//                textColor = MaterialTheme.colorScheme.onError, //jeg likte egt hvor mørk den her var, men passer dårlig m kontrast. kan evt bruke en if setning og kun bruke "onError" for darkmode og "error" for frost og light
//                iconColor = MaterialTheme.colorScheme.onError,
                iconColor = MaterialTheme.colorScheme.error,
            )
        } else {
            IconTextRow(
                iconRes = R.drawable.baseline_battery_charging_full_24,
                text = "%.2f kWh".format(animatedEnergy),
                textStyle = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(16.dp),
                textColor = energyColor,
                iconColor = energyColor
            )
        }
        EnergyFlowAnimationDown()
        HouseAnimation()
        EnergyFlowDown()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FlowRow(
                mainAxisSpacing = 12.dp,
                crossAxisSpacing = 12.dp,
            ) {
                devices.forEach { (name, value) ->
                    val connected = connectedDevices.containsKey(name)
                    val glowAlpha by animateFloatAsState(
                        targetValue = if (connected) 1f else 0f,
                        animationSpec = tween(durationMillis = 500)
                    )

                    OutlinedCard(
                        modifier = Modifier
                            .width(180.dp)
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
                                    connectedDevices =
                                        connectedDevices.toMutableMap().apply { remove(name) }
                                    currentEnergy += value
                                } else {
                                    connectedDevices = connectedDevices.toMutableMap()
                                        .apply { put(name, value) }
                                    currentEnergy -= value
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (connected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant

                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val iconRes = deviceIcons[name] ?: R.drawable.baseline_battery_6_bar_24

                            IconTextRow(
                                iconRes = iconRes,
                                text = name,
                                textStyle = MaterialTheme.typography.bodyMedium,
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "%.2f kWh".format(value),
                                fontWeight = if (connected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun EnergyFlowAnimationDown() {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("energy_down.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .height(75.dp)
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(100.dp / 30.dp)
        )
    }
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

    Box(
        modifier = Modifier
            .height(200.dp)
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
                .aspectRatio(400.dp / 300.dp),
        )
    }
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