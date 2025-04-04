package no.solcellepaneller.ui.electricity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.Bar
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarPlotData
import co.yml.charts.ui.barchart.models.BarStyle
import no.solcellepaneller.model.electricity.ElectricityPrice
import java.time.ZonedDateTime

@Composable
fun ElectricityPriceChart(prices: List<ElectricityPrice>) {
    var chartType by remember { mutableStateOf(ChartType.LINE) }

    val points = prices.mapIndexed { index, price ->
        val hour = ZonedDateTime.parse(price.time_start).hour / 2
        Point(hour.toFloat(), price.NOK_per_kWh.toFloat())
    }

    val bars = points.map { point ->
        Bar(
            point = point,
            color = Color.Blue
        )
    }

    val totalHours = points.size

    //Prepare X-axis (hours)
    val xAxisData = AxisData.Builder()
        .axisStepSize(20.dp)
        .steps(totalHours / 2)
        .labelData { i -> "${(i * 2) % 24}:00" }
        .axisLabelAngle(45f)
        .build()

    //Prepare Y-axis
    val maxPrice = prices.maxOf { it.NOK_per_kWh }
    val minPrice = prices.minOf { it.NOK_per_kWh }
    val yAxisData = AxisData.Builder()
        .topPadding(20.dp)
        .labelData { i -> "%.2f".format(i.toDouble()) }
        .axisStepSize(((maxPrice - minPrice).toFloat() / 5).dp)
        .build()

    Column {
        Button(
            onClick = {
                chartType = if (chartType == ChartType.LINE) ChartType.BAR else ChartType.LINE
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = if (chartType == ChartType.LINE) "Vis søylediagram" else "Vis linjediagram")
        }

        when (chartType) {
            ChartType.LINE -> {
                val lineChartData = LineChartData(
                    linePlotData = LinePlotData(
                        lines = listOf(
                            Line(
                                dataPoints = points,
                                LineStyle(color = Color.Blue),
                                IntersectionPoint(color = Color.Red),
                                SelectionHighlightPoint(color = Color.Yellow),
                                ShadowUnderLine(
                                    alpha = 0.5f,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color.Cyan,
                                            Color.Transparent)
                                    )
                                ),
                                SelectionHighlightPopUp()
                            )
                        )
                    ),
                    xAxisData = xAxisData,
                    yAxisData = yAxisData,
                    gridLines = GridLines(color = Color.LightGray),
                    backgroundColor = Color.White
                )

                LineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .padding(16.dp),
                    lineChartData = lineChartData
                )
            }

            ChartType.BAR -> {
                val barChartData = BarChartData(
                    chartData = BarPlotData(
                        barList = listOf(bars),
                        barStyle = BarStyle(
                            paddingBetweenBars = 8.dp,
                            barWidth = 16.dp
                        )
                    ),
                    xAxisData = xAxisData,
                    yAxisData = yAxisData,
                    gridLines = GridLines(color = Color.LightGray),
                    backgroundColor = Color.White
                )

                BarChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .padding(16.dp),
                    barChartData = barChartData
                )
            }
        }
    }
}