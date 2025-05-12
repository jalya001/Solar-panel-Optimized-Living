package no.solcellepanelerApp.ui.price

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import no.solcellepanelerApp.model.price.ElectricityPrice
import no.solcellepanelerApp.ui.theme.ThemeMode
import no.solcellepanelerApp.ui.theme.ThemeState
import java.time.ZonedDateTime

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ElectricityPriceChart(prices: List<ElectricityPrice>) {
    val selectedPoint = remember { mutableStateOf<Point?>(null) }

    val points = prices.map { price ->
        val hour = ZonedDateTime.parse(price.time_start).hour
        Point(hour.toFloat(), price.NOK_per_kWh.toFloat())
    }

    // Prepare X-axis (hours)
    val xAxisData = AxisData.Builder()
        .axisStepSize(12.dp)
        .steps(prices.size / 2) // Showing fewer steps
        .labelData { i -> if (i % 2 == 0) "%02d".format(i) else "" }
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelAngle(40f)
        .bottomPadding(32.dp)
        .build()

    // Prepare Y-axis (price)
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

    // Card and chart display
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
        Box(
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
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
        ) {
            val lineChartData = LineChartData(
                linePlotData = LinePlotData(
                    lines = listOf(
                        Line(
                            dataPoints = points,
                            LineStyle(
                                color = if (ThemeState.themeMode == ThemeMode.DARK) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                            ),
                            IntersectionPoint(
                                color = if (ThemeState.themeMode == ThemeMode.DARK) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                            ),
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

            // X-axis name
            Text(
                text = stringResource(R.string.x_axis_name),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-5).dp),
                color = MaterialTheme.colorScheme.tertiary
            )

            // Y-axis name
            Text(
                text = stringResource(R.string.y_axis_name),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-28).dp, y = (-5).dp)
                    .rotate(-90f),
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}
