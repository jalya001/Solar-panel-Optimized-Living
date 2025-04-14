package no.solcellepaneller.ui.reusables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.solcellepaneller.R

@Composable
fun MyCard(
    text: String = "",
    modifier: Modifier = Modifier,
//    size: DpSize = DpSize(width = 240.dp, height = 100.dp),
    width: Dp = 240.dp,
    style: String = "",
    elevation: Dp = 3.dp,
    content: (@Composable () -> Unit)? = null,
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            contentColor = MaterialTheme.colorScheme.tertiary,
            containerColor = MaterialTheme.colorScheme.secondary
        ),
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
                if (style == "Large") {
                    Text(
                        text = text,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )
                } else {
                    Text(
                        text = text,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
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
    style: String = "",
    content: (@Composable () -> Unit)? = null,
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            contentColor = MaterialTheme.colorScheme.tertiary,
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .padding(8.dp)
            .size(size),
        onClick = { navController.navigate(route) },

        ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (content != null) {
                content()
            } else {
                if (style == "Large") {
                    Text(
                        text = text,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )
                } else {
                    Text(
                        text = text,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
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
    energyPrice: Double
) {
    val cardModifier = modifier
        .fillMaxWidth()

    MyCard(
        modifier = cardModifier,
        elevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                    modifier = modifier.size(30.dp),
                    contentDescription = null
                )
                Text(
                    month,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Row {
                Icon(
                    painter = painterResource(id = R.drawable.rounded_nest_sunblock_24),
                    modifier = modifier.size(30.dp),
                    contentDescription = null
                )
                Text(
                    "Global Radiation: %.2f".format(radiation),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_cloud_24),
                    modifier = modifier.size(30.dp),
                    contentDescription = null
                )
                Text(
                    "Avg Cloud Cover: %.2f".format(cloud / 8),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.outline_mode_cool_24),
                    modifier = modifier.size(30.dp),
                    contentDescription = null
                )
                Text(
                    "Avg Snow Cover: %.2f".format(snow / 4),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_device_thermostat_24),
                    modifier = modifier.size(30.dp),
                    contentDescription = null
                )
                Text(
                    "Temp Factor: %.2f °C".format(1 + (-0.44) * (temp - 25)),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.rounded_nest_sunblock_24),
                    modifier = modifier.size(30.dp),
                    contentDescription = null
                )
                Text(
                    "Adj. Radiation: %.2f kWh/m²".format(adjusted),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_battery_6_bar_24),
                    modifier = modifier.size(30.dp),
                    contentDescription = null
                )
                Text(
                    "Estimated Energy: %.2f kWh".format(energy),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_power_24),
                    modifier = modifier.size(30.dp),
                    contentDescription = null
                )
                Text(
                    "Power/hour: %.2f kW".format(power),
                    style = MaterialTheme.typography.bodyLarge
                )
            }





            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        navController.navigate("monthly_savings/$month/${energy}/${energyPrice}")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = modifier.weight(1f)
                ) {
                    Text("Show savings $month")
                }
                //Button(
                //    onClick = {
                //        navController.navigate("produce/${energy}")
                //    },
                //    colors = ButtonDefaults.buttonColors(
                //        containerColor = MaterialTheme.colorScheme.tertiary,
                //        contentColor = MaterialTheme.colorScheme.background
                //    ),
                //    modifier = modifier.weight(1f)
                //) {
                //    Text("Show available energy")
                //}
            }
        }
    }
}
