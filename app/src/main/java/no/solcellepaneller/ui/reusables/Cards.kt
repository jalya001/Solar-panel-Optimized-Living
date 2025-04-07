package no.solcellepaneller.ui.reusables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MyCard(
    text: String="",
    modifier: Modifier = Modifier,
//    size: DpSize = DpSize(width = 240.dp, height = 100.dp),
    width: Dp =240.dp,
    style: String="",
    elevation: Dp = 3.dp,
    content: (@Composable () -> Unit)? = null
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            contentColor = MaterialTheme.colorScheme.tertiary,
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        modifier = modifier.padding(8.dp).width(width),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            if (content != null) {
                content()
            }else{
            if (style=="Large"){
                Text(
                    text = text,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }else{
                Text(
                    text = text,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }}
        }
    }
}

@Composable
fun MyNavCard(
    text: String= "",
    route: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    size: DpSize = DpSize(width = 240.dp, height = 100.dp),
    style: String="",
    content: (@Composable () -> Unit)? = null
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            contentColor = MaterialTheme.colorScheme.tertiary,
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier.padding(8.dp).size(size),
        onClick = { navController.navigate(route) },

        ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            if (content != null) {
                content()
            }else{
            if (style=="Large"){
                Text(
                    text = text,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }else{
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
    isMultiMonth: Boolean = false,
    modifier: Modifier = Modifier
) {
    val cardModifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 0.dp)
        .then(
            if (isMultiMonth) Modifier.padding(vertical = 6.dp)
            else Modifier.padding(vertical = 130.dp)
        )

    MyCard(
        modifier = cardModifier,
        elevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📅 $month", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Text("☀️ Global Radiation: %.2f".format(radiation), style = MaterialTheme.typography.bodyLarge)
            Text("☁️ Avg Cloud Cover: %.2f".format(cloud / 8), style = MaterialTheme.typography.bodyLarge)
            Text("❄️ Avg Snow Cover: %.2f".format(snow / 4), style = MaterialTheme.typography.bodyLarge)
            Text("🌡️ Temp Factor: %.2f °C".format(1 + (-0.44) * (temp - 25)), style = MaterialTheme.typography.bodyLarge)
            Text("🔆 Adj. Radiation: %.2f kWh/m²".format(adjusted), style = MaterialTheme.typography.bodyLarge)
            Text("⚡ Estimated Energy: %.2f kWh".format(energy), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            Text("🔌 Power/hour: %.2f kW".format(power), style = MaterialTheme.typography.bodyLarge)
        }
    }
}
