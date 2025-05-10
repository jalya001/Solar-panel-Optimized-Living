package no.solcellepanelerApp.ui.savings

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import no.solcellepanelerApp.model.electricity.ChartType
import no.solcellepanelerApp.ui.reusables.AppScaffoldController
import no.solcellepanelerApp.ui.reusables.IconTextRow
import no.solcellepanelerApp.ui.reusables.SimpleTutorialOverlay
import no.solcellepanelerApp.ui.theme.ThemeMode
import no.solcellepanelerApp.ui.theme.ThemeState

@SuppressLint("MutableCollectionMutableState", "DefaultLocale")
@Composable
fun SavingsScreen(
    isMonthly: Boolean, // Toggle between monthly and yearly view
    month: String = "", // Only needed for monthly view
    energyProduced: Double,
    energyPrice: Double,
    appScaffoldController: AppScaffoldController,
    contentPadding: PaddingValues,
    savingsViewModel: SavingsViewModel = viewModel(),
) {
    val savings by savingsViewModel.savings.collectAsState()
    val currentEnergy by savingsViewModel.currentEnergy.collectAsState()
    val connectedDevices by savingsViewModel.connectedDevices.collectAsState()
    val weather = savingsViewModel.weatherDataFlow.collectAsState().value ?: emptyMap()
    val calculationResult by savingsViewModel.calculationResults.collectAsState()

    val devices = savingsViewModel.devices
    val deviceIcons = savingsViewModel.deviceIcons

    var showOverlay by remember { mutableStateOf(true) }

    val animatedEnergy = animateFloatAsState(
        targetValue = currentEnergy.toFloat(),
        animationSpec = tween(durationMillis = 500, easing = LinearEasing)
    ).value

    val energyColor = animateColorAsState(
        targetValue = if (currentEnergy < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing)
    ).value

    val scrollState = rememberScrollState()
    val showHeaderText by remember {
        derivedStateOf {
            scrollState.value < 2000
        }
    }

    val monthlyTitle = stringResource(R.string.monthly_savings, month)
    val yearlyTitle = stringResource(R.string.yearly_savings)

    LaunchedEffect(Unit) {
        val screenTitle = if (isMonthly)
            monthlyTitle
        else
            yearlyTitle
        appScaffoldController.setTopBar(screenTitle)
    }

    LaunchedEffect(energyProduced, energyPrice) {
        savingsViewModel.initialize(energyProduced, energyPrice)
    }

    if (showOverlay) {
        SimpleTutorialOverlay(
            onDismiss = { showOverlay = false },
            "Se hvor mye du sparer \n\n Trykk på de ulike enhetene for å se *trenger en god formulering* \n\nScroll opp for mer informasjon!"
        )
    }

    Box(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            AnimatedVisibility(
                visible = showHeaderText,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isMonthly) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = MaterialTheme.typography.headlineSmall.toSpanStyle()
                                        .copy(fontWeight = FontWeight.ExtraLight)
                                ) {
                                    append(stringResource(R.string.monthly_savings_prefix))
                                }
                                append(
                                    AnnotatedString(
                                        String.format(" %.2f kroner ", savings),
                                        MaterialTheme.typography.headlineSmall.toSpanStyle()
                                    )
                                )
                                withStyle(
                                    style = MaterialTheme.typography.headlineSmall.toSpanStyle()
                                        .copy(fontWeight = FontWeight.ExtraLight)
                                ) {
                                    append(stringResource(R.string.monthly_savings_suffix_part1))
                                    append(month)
                                    append(stringResource(R.string.monthly_savings_suffix_part2))
                                }
                            },
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = MaterialTheme.typography.headlineSmall.toSpanStyle()
                                        .copy(fontWeight = FontWeight.ExtraLight)
                                ) {
                                    append(stringResource(R.string.yearly_savings_prefix))
                                }
                                append(
                                    AnnotatedString(
                                        String.format(" %.2f kroner ", savings),
                                        MaterialTheme.typography.headlineSmall.toSpanStyle()
                                    )
                                )
                                withStyle(
                                    style = MaterialTheme.typography.headlineSmall.toSpanStyle()
                                        .copy(fontWeight = FontWeight.ExtraLight)
                                ) {
                                    append(stringResource(R.string.yearly_savings_suffix))
                                }
                            },
                            textAlign = TextAlign.Center
                        )
                    }

                    if (currentEnergy < 0) {
                        IconTextRow(
                            iconRes = R.drawable.baseline_battery_charging_full_24,
                            text = "Energy deficit! %.2f kWh".format(animatedEnergy),
                            textStyle = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp),
                            textColor = MaterialTheme.colorScheme.error,
                            iconColor = MaterialTheme.colorScheme.error,
                        )
                    } else {
                        IconTextRow(
                            iconRes = R.drawable.baseline_battery_charging_full_24,
                            text = "%.2f kWh".format(animatedEnergy),
                            textStyle = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp),
                            textColor = energyColor,
                            iconColor = energyColor
                        )
                        EnergyFlowAnimationDown()
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
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
                                            color = MaterialTheme.colorScheme.primary.copy(
                                                alpha = glowAlpha
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .shadow(
                                            elevation = if (connected) 8.dp else 2.dp,
                                            shape = RoundedCornerShape(12.dp),
                                            ambientColor = MaterialTheme.colorScheme.primary.copy(
                                                alpha = glowAlpha
                                            )
                                        )
                                        .clickable {
                                            savingsViewModel.toggleDevice(name)
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
                                        val iconRes = deviceIcons[name]
                                            ?: R.drawable.baseline_battery_6_bar_24

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

                if (isMonthly) {
                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        MonthlyChartSection(
                            title = stringResource(R.string.snow_full_info),
                            data = weather["mean(snow_coverage_type P1M)"] ?: emptyArray(),
                            unit = "%"
                        )

                        MonthlyChartSection(
                            title = stringResource(R.string.cloud_full_info),
                            data = weather["mean(cloud_area_fraction P1M)"] ?: emptyArray(),
                            unit = "%"
                        )

                        MonthlyChartSection(
                            title = stringResource(R.string.temp_full_info),
                            data = weather["mean(air_temperature P1M)"] ?: emptyArray(),
                            unit = "°C"
                        )


                        val global = weather["mean(PVGIS_radiation P1M)"] ?: emptyArray()
                        val adjusted =
                            calculationResult?.adjustedRadiation?.toTypedArray() ?: emptyArray()
                        val diff = if (global.size == adjusted.size) {
                            Array(global.size) { i -> global[i] - adjusted[i] }
                        } else emptyArray()

                        MultiLineChart(
                            datasets = listOf(
                                "Global Radiation" to global,
                                "Adjusted Radiation" to adjusted,
                                "Difference" to diff
                            ),
                            measure = "W/m²"
                        )

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
                .fillMaxSize()
                .padding(top = 20.dp)
        )
    }
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


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun Chart(data: Array<Double>, measure: String = "cm") {
    val isPercentage = measure == "%"

    val prosessData = if (isPercentage) {
        val max = data.maxOrNull() ?: 1.0
        data.map { (it / max) * 100 }.toTypedArray()
    } else data

    ChartType.LINE
    val selectedPoint = remember { mutableStateOf<Point?>(null) }

    val points = prosessData.mapIndexed { index, value ->
        Point(((index).toFloat()), value.toFloat())
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

    val monthLabels = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    val xAxisData = AxisData.Builder()
        .axisStepSize(25.dp)
        .steps(monthLabels.size - 1)
        .labelData { index ->
            monthLabels.getOrNull(index) ?: ""
        }
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelAngle(0f)
        .axisLabelAngle(45f)
        .bottomPadding(40.dp)
        .axisOffset(10.dp)
        .startPadding(50.dp)

        .build()

    val maxDepth = prosessData.maxOrNull() ?: 0.0
    val minDepth = prosessData.minOrNull() ?: 0.0
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
                val monthIndx = it.x.toInt()
                val month = monthLabels.getOrNull(monthIndx) ?: "?"
                val depth = it.y
                Text(
                    text = "Måned $month: %.1f %s".format(depth, measure),
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
                            selectionHighlightPopUp = SelectionHighlightPopUp { _, point ->
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

@Composable
fun MonthlyChartSection(
    title: String,
    data: Array<Double>,
    unit: String
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Chart(data = data, measure = unit)
    }
}

@Composable
fun MultiLineChart(
    datasets: List<Pair<String, Array<Double>>>,
    measure: String = "W/m²"
) {
    val monthLabels = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    val isPercentage = measure == "%"
    val selectedX = remember { mutableStateOf<Int?>(null) }
    val processedDatasets = datasets.map { (label, data) ->
        val processed = if (isPercentage) {
            val max = data.maxOrNull() ?: 1.0
            data.map { (it / max) * 100 }.toTypedArray()
        } else data
        label to processed
    }

    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        Color.Red,
        Color.Green,
        Color.Magenta
    )

    val lines = processedDatasets.mapIndexed { index, (_, data) ->
        Line(
            dataPoints = data.mapIndexed { i, value -> Point(i.toFloat(), value.toFloat()) },
            lineStyle = LineStyle(color = colors[index % colors.size]),
            intersectionPoint = IntersectionPoint(color = colors[index % colors.size]),
            selectionHighlightPoint = SelectionHighlightPoint(
                color = colors[index % colors.size],
                isHighlightLineRequired = true
            ),
            shadowUnderLine = ShadowUnderLine(
                alpha = 0.3f,
                brush = Brush.verticalGradient(
                    listOf(colors[index % colors.size], Color.Transparent)
                )
            ),
            selectionHighlightPopUp = SelectionHighlightPopUp { _, point ->
                selectedX.value = point.x.toInt()
            }
        )
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(25.dp)
        .steps(monthLabels.size - 1)
        .labelData { index -> monthLabels.getOrNull(index) ?: "" }
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelAngle(45f)
        .build()

    val allValues = processedDatasets.flatMap { it.second.toList() }
    val minY = allValues.minOrNull() ?: 0.0
    val maxY = allValues.maxOrNull() ?: 1.0
    val stepSize = ((maxY - minY) / 5).coerceAtLeast(1.0)

    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelData { i -> "%.1f".format(minY + stepSize * i) }
        .axisStepSize(50.dp)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val chartData = LineChartData(
        linePlotData = LinePlotData(lines = lines),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = MaterialTheme.colorScheme.background
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(550.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Text(
            text = stringResource(R.string.difference_info),
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            selectedX.value?.let {
                val valueText = StringBuilder()
                processedDatasets.forEachIndexed { index, (label, data) ->
                    if (it in data.indices) {
                        val value = data[it]
                        valueText.append("$label: %.1f $measure".format(value))
                        if (index < processedDatasets.size - 1) valueText.append("  |  ")
                    }
                }
                Text(
                    text = valueText.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LineChart(
            modifier = Modifier
                .fillMaxWidth(),

            lineChartData = chartData
        )
    }
}
