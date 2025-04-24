package no.solcellepanelerApp.ui.electricity

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.solcellepanelerApp.model.electricity.ElectricityPrice
import no.solcellepanelerApp.model.electricity.Region
import no.solcellepanelerApp.ui.handling.LoadingScreen
import no.solcellepanelerApp.ui.home.LightningAnimation
import no.solcellepanelerApp.ui.theme.ThemeMode
import no.solcellepanelerApp.ui.theme.ThemeState
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun PriceCard(prices: List<ElectricityPrice>) {
    val currentHour = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).hour

    val currentPrice = prices.find { price ->
        val startTime = ZonedDateTime.parse(price.time_start)
        startTime.hour == currentHour
    } ?: run {
        Log.e("ERROR", "Fant ingen pris for nåværende time!")
        null
    }

    val highestPrice = prices.maxByOrNull { it.NOK_per_kWh }
    val lowestPrice = prices.minByOrNull { it.NOK_per_kWh }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            currentPrice?.let {
                PriceRow(
                    icon = Icons.Default.AccessTime,
                    label = "Pris nå",
                    price = it.NOK_per_kWh,
                    time = it.getTimeRange()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            highestPrice?.let {
                PriceRow(
                    icon = Icons.Default.ArrowUpward,
                    label = "Høyeste pris",
                    price = it.NOK_per_kWh,
                    time = it.getTimeRange()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            lowestPrice?.let {
                PriceRow(
                    icon = Icons.Default.ArrowDownward,
                    label = "Laveste pris",
                    price = it.NOK_per_kWh,
                    time = it.getTimeRange()
                )
            }
        }
    }
}

@Composable
fun PriceRow(
    icon: ImageVector,
    label: String,
    price: Double,
    time: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (ThemeState.themeMode == ThemeMode.DARK) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = "$label:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp
            )

            Text(
                text = "${"%2f".format(price)} NOK/kWh",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tid: $time",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun HomePriceCard(
    prices: List<ElectricityPrice>,
    selectedRegion: Region?
) {
    if (selectedRegion == null) {
        LoadingScreen()
        return
    }

    val currentHour = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).hour

    val currentPrice = prices.find { price ->
        val startTime = ZonedDateTime.parse(price.time_start)
        startTime.hour == currentHour
    } ?: run {
        Log.e("ERROR", "Fant ingen pris for nåværende time!")
        null
    }

    currentPrice?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Top,
            //horizontalAlignment = Alignment.
        ) {
            Text(
                text = "Strømpris akkurat nå i ${selectedRegion.name}:",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            PriceRow(
                icon = Icons.Default.AccessTime,
                label = "Pris nå",
                price = it.NOK_per_kWh,
                time = it.getTimeRange()
            )
            LightningAnimation()
        }
    }
}
