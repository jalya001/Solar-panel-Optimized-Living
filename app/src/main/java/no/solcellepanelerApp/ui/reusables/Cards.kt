package no.solcellepanelerApp.ui.reusables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.solcellepanelerApp.R

@Composable
fun MyCard(
    text: String = "",
    modifier: Modifier = Modifier,
    width: Dp = 240.dp,
    style: TextStyle,
    elevation: Dp = 3.dp,
    content: (@Composable () -> Unit)? = null,
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        modifier = modifier
            .padding(8.dp)
            .width(width),
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            if (content != null) {
                content()
            } else {
                Text(
                    text = text,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = style
                )
            }
        }
    }
}

@Composable
fun MyNavCard(
    text: String = "",
    route: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    size: DpSize = DpSize(width = 240.dp, height = 100.dp),
    style: TextStyle,
    content: (@Composable () -> Unit)? = null,
    color: Color,
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .padding(8.dp)
            .size(size),
        onClick = { navController.navigate(route) },

        ) {
        Box(modifier = Modifier.fillMaxSize()) {

            content?.let {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    it()
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    style = style,
                    modifier = Modifier.padding(16.dp),
                    color = color
                )
            }
        }
    }
}

@Composable
fun DataCard(
    month: String,
    radiation: Double,
    cloud: Double,
    snow: Double,
    temp: Double,
    adjusted: Double,
    energy: Double,
    power: Double,
    modifier: Modifier = Modifier,
    navController: NavController,
    energyPrice: Double,
    allMonths: Boolean,
) {
    val cardModifier = modifier
        .fillMaxWidth()

    MyCard(
        modifier = cardModifier,
        elevation = 4.dp,
        style = MaterialTheme.typography.bodyLarge,
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (allMonths) {
                IconTextRow(
                    iconRes = R.drawable.baseline_calendar_month_24,
                    text = "$month",
                    textStyle = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            IconTextRow(
                iconRes = R.drawable.rounded_nest_sunblock_24,
                text = "Global Radiation: %.2f".format(radiation)
            )

            IconTextRow(
                iconRes = R.drawable.baseline_cloud_24,
                text = "Avg Cloud Cover: %.2f".format(cloud / 8)
            )

            IconTextRow(
                iconRes = R.drawable.outline_mode_cool_24,
                text = "Avg Snow Cover: %.2f".format(snow / 4)
            )

            IconTextRow(
                iconRes = R.drawable.baseline_device_thermostat_24,
                text = "Temp Factor: %.2f °C".format(1 + (-0.44) * (temp - 25))
            )

            IconTextRow(
                iconRes = R.drawable.rounded_nest_sunblock_24,
                text = "Adj. Radiation: %.2f kWh/m²".format(adjusted)
            )

            IconTextRow(
                iconRes = R.drawable.baseline_battery_6_bar_24,
                text = "Estimated Energy: %.2f kWh".format(energy),
                fontWeight = FontWeight.Bold
            )

            IconTextRow(
                iconRes = R.drawable.baseline_power_24,
                text = "Power/hour: %.2f kW".format(power)
            )

            Button(
                onClick = {
                    navController.navigate("monthly_savings/$month/${energy}/${energyPrice}")
                },
                modifier = modifier.fillMaxWidth()
            ) {
                Text("Show your savings in $month")
            }
        }
    }
}

@Composable
fun ModeCard(
    label: String,
    iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
//    val border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null

    ElevatedCard(
        modifier = Modifier
            .width(120.dp)
            .height(60.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
fun SavingsMonth_Card(
    label: String,
    iconRes: Int,
    onClick: () -> Unit,
) {
//    val border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null

    ElevatedCard(
        modifier = Modifier
            .width(200.dp)
            .height(60.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}


@Composable
fun IconTextRow(
    iconRes: Int,
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    fontWeight: FontWeight? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = modifier.size(30.dp),
            tint = MaterialTheme.colorScheme.tertiary
        )
        Text(
            text = text,
            style = textStyle,
            fontWeight = fontWeight
        )
    }
}
