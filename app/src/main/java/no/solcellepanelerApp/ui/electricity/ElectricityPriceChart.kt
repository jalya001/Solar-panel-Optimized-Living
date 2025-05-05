package no.solcellepanelerApp.ui.electricity

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import no.solcellepanelerApp.R
import no.solcellepanelerApp.model.electricity.ElectricityPrice
import no.solcellepanelerApp.ui.theme.ThemeMode
import no.solcellepanelerApp.ui.theme.ThemeState
import java.time.ZonedDateTime

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ElectricityPriceChart(prices: List<ElectricityPrice>) {
    var chartType by remember { mutableStateOf(ChartType.LINE) }
    val selectedPoint = remember { mutableStateOf<Point?>(null) }

    val points = prices.map { price ->
        val hour = ZonedDateTime.parse(price.time_start).hour
        Point(hour.toFloat(), price.NOK_per_kWh.toFloat())
    }

    val barColor =
        if (ThemeState.themeMode == ThemeMode.DARK) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary

    val bars = prices.map { price ->
        val hour = ZonedDateTime.parse(price.time_start).hour
        BarData(
            point = Point(hour.toFloat(), price.NOK_per_kWh.toFloat()),
            label = "%02d:00".format(hour),
            color = barColor
        )
    }

    //Prepare X-axis (hours) - Showing fewer labels to prevent crowding
    val xAxisData = AxisData.Builder()
        .axisStepSize(12.dp)
        .steps(prices.size / 2)//Showing fewer steps
        .labelData { i -> if (i % 2 == 0) "%02d".format(i) else "" }
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelAngle(40f)
        .bottomPadding(32.dp)
        .build()

    //Prepare Y-axis (price)
    val maxPrice = prices.maxOf { it.NOK_per_kWh }
    val minPrice = prices.minOf { it.NOK_per_kWh }

    val steps = 1
    val stepSize = ((maxPrice - minPrice) / steps).coerceAtLeast(0.1)

    val yAxisData = AxisData.Builder()
        .steps(steps)
        .topPadding(20.dp)
        .labelData { i ->
            "%.2f".format((minPrice + stepSize * i).toFloat())
        }
        .axisStepSize(((maxPrice - minPrice) / 5).toFloat().dp)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .build()

    Button(
        onClick = {
            chartType = if (chartType == ChartType.LINE) ChartType.BAR else ChartType.LINE
        },
        modifier = Modifier.padding(8.dp)
    ) {
        Text(text = if (chartType == ChartType.LINE) stringResource(R.string.bar_chart) else stringResource(R.string.line_chart))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary
        )
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Show selected point's time and price
            selectedPoint.value?.let {
                val hour = it.x.toInt()
                val price = it.y
                val timeString = "Kl. %02d:00".format(hour)
                val priceString = "Pris: %.2f kr".format(price)

                Text(
                    text = "$timeString, $priceString",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(bottom = 1.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .height(320.dp)
                //.padding(start = 4.dp, end = 4.dp)
        ) {
            when (chartType) {
                ChartType.LINE -> {
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
                            ),
                        ),
                        xAxisData = xAxisData,
                        yAxisData = yAxisData,
                        gridLines = GridLines(color = Color.LightGray),
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        bottomPadding = 30.dp
                    )

                    LineChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .height(300.dp),
                        lineChartData = lineChartData
                    )
                }

                ChartType.BAR -> {
                    val barChartData = BarChartData(
                        chartData = bars,
                        xAxisData = xAxisData,
                        yAxisData = yAxisData,
                        barStyle = BarStyle(
                            paddingBetweenBars = 1.5.dp,
                            barWidth = 10.dp
                        ),
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        paddingEnd = 16.dp
                    )

                    BarChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .height(300.dp),
                        barChartData = barChartData
                    )
                }
            }




            // X-axis name
            Text(
                text = stringResource(R.string.x_axis_name),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -5.dp),
                color = MaterialTheme.colorScheme.tertiary
            )

            // Y-axis name
            Text(
                text = stringResource(R.string.y_axis_name),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-28).dp, y = -5.dp)
                    .rotate(-90f),
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}