package no.solcellepanelerApp.ui.result

//import androidx.compose.foundation.layout.FlowRow
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.flowlayout.FlowRow
import no.solcellepanelerApp.R
import no.solcellepanelerApp.ui.electricity.ChartType
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.reusables.IconTextRow
import no.solcellepanelerApp.ui.theme.ThemeMode
import no.solcellepanelerApp.ui.theme.ThemeState


@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShowProduce(
    energy: Double,
    weatherData:Map<String, Array<Double>> = emptyMap(),
    navController: NavController,
    fontScaleViewModel: FontScaleViewModel,

) {
    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }
    var currentEnergy by remember { mutableStateOf(energy) }
    Log.e("snowList", weatherData.toString())
    //dette må oversettes
    val devices = listOf(

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

    val preConnected = listOf("Fridge", "TV", "Laptop")

    var connectedDevices by remember {
        mutableStateOf(
            devices
                .filter { it.first in preConnected }
                .associate { it.first to it.second }
                .toMutableMap()
        )
    }
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
            EnergyFlowAnimationDown()
        }



        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            HouseAnimation()
            EnergyFlowDown()
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
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.snow_info),style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold)
            Chart(weatherData["mean(snow_coverage_type P1M)"] ?: emptyArray(),"")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.cloud_info),style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold)
            Chart(weatherData["mean(cloud_area_fraction P1M)"] ?: emptyArray(),"")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.temp_info),style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold)
            Chart(weatherData["mean(air_temperature P1M)"] ?: emptyArray(),"°C")
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

//@Composable
//fun EnergyFlowAnimationUp() {
//    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("energy_down.json"))
//    val progress by animateLottieCompositionAsState(
//        composition,
//        iterations = LottieConstants.IterateForever
//    )
//
//    LottieAnimation(
//        composition = composition,
//        progress = { progress },
//        modifier = Modifier
//            .height(100.dp)
//            .graphicsLayer(
//                rotationZ = 180f // rotate 180 degrees
//            )
//    )
//
//}

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
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun Chart(data: Array<Double>, measure: String = "cm") {
    ChartType.LINE
    val selectedPoint = remember { mutableStateOf<Point?>(null) }

    val points = data.mapIndexed { index, value ->
        Point(((index +1).toFloat()), value.toFloat())
    }

    val barColor =
        if (ThemeState.themeMode == ThemeMode.DARK) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary

    data.mapIndexed { index, value ->
        BarData(
            point = Point((index + 1).toFloat(), value.toFloat()),
            label = "M${index + 1}",
            color = barColor
        )
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(20.dp)
        .steps(12)
        .labelData { i -> if (i in 1..12) "$i" else "" }
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelAngle(0f)
        .bottomPadding(32.dp)
        .build()
    
    val maxDepth = data.maxOrNull() ?: 0.0
    val minDepth = data.maxOrNull() ?: 0.0
    val stepSize = ((maxDepth - minDepth) / 5).coerceAtLeast(1.0)

    val yAxisData = AxisData.Builder()
        .steps(5)
        .topPadding(20.dp)
        .labelData { i -> "%.1f".format(minDepth + stepSize * i) }
        .axisStepSize(stepSize.toFloat().dp)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .build()



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            selectedPoint.value?.let {
                val month = it.x.toInt()
                val depth = it.y
                Text(
                    text = "Måned $month: %.1f $measure".format(depth),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        BoxWithConstraints(modifier = Modifier.height(320.dp)) {
                    val lineChartData = LineChartData(
                        linePlotData = LinePlotData(
                            lines = listOf(
                                Line(
                                dataPoints = points,
                                LineStyle(color = if (ThemeState.themeMode == ThemeMode.DARK) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary),
                                IntersectionPoint(color = if (ThemeState.themeMode == ThemeMode.DARK) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary),
                                SelectionHighlightPoint(
                                    color = if (ThemeState.themeMode == ThemeMode.DARK) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                    draw = { offset ->
                                        drawCircle(
                                            color = if (ThemeState.themeMode == ThemeMode.DARK) {
                                                Color(0xFFF3BD6E)
                                            } else {
                                                Color(0xFF00696D)
                                            },
                                            radius = 6.dp.toPx(),
                                            center = offset
                                        )
                                    },
                                    isHighlightLineRequired = true
                                ),
                                ShadowUnderLine(
                                    alpha = 0.5f,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            if (ThemeState.themeMode == ThemeMode.DARK) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                            Color.Transparent
                                        )
                                    )
                                ),
                                selectionHighlightPopUp = SelectionHighlightPopUp { offset, point ->
                                    selectedPoint.value = point
                                }
                            )
                            )
                        ),
                        xAxisData = xAxisData,
                        yAxisData = yAxisData,
                        isZoomAllowed = true,
                        backgroundColor = MaterialTheme.colorScheme.background,

                    )
                    LineChart(modifier = Modifier.fillMaxSize(), lineChartData = lineChartData)
                }

            }
        }


