package no.solcellepanelerApp.ui.electricity

import android.util.Log
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import no.solcellepanelerApp.R
import no.solcellepanelerApp.model.electricity.ElectricityPrice
import no.solcellepanelerApp.model.electricity.Region
import no.solcellepanelerApp.ui.handling.LoadingScreen
import no.solcellepanelerApp.ui.home.ElectricityTowers
import no.solcellepanelerApp.ui.theme.ThemeMode
import no.solcellepanelerApp.ui.theme.ThemeState
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun PriceCard(
    prices: List<ElectricityPrice>,
    hourIndex: Int,
    onHourChange: (Int) -> Unit,
) {
    val currentHour = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).hour

    val currentDisplayedPrice = prices.getOrNull(hourIndex)

    prices.find { price ->
        val startTime = ZonedDateTime.parse(price.time_start)
        startTime.hour == currentHour
    } ?: run {
        Log.e("ERROR", "Fant ingen pris for nåværende time!")
        null
    }

    val highestPrice = prices.maxByOrNull { it.NOK_per_kWh }
    val lowestPrice = prices.minByOrNull { it.NOK_per_kWh }

    val label = when {
        ZonedDateTime.parse(currentDisplayedPrice?.time_start).hour == currentHour -> stringResource(R.string.price_now_card)
        hourIndex > currentHour ->
            stringResource(R.string.future_price_prefix) +
                " ${hourIndex - currentHour} " +
                    stringResource(R.string.price_suffix_part1) +
                    (if (hourIndex - currentHour > 1) stringResource(R.string.price_suffix_part2) else "")
        else ->
            stringResource(R.string.past_price_prefix) +
                " ${currentHour - hourIndex} " +
                    stringResource(R.string.price_suffix_part1) +
                    (if (currentHour - hourIndex > 1) stringResource(R.string.price_suffix_part2) else "") + stringResource(R.string.past_price_suffix)
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                currentDisplayedPrice?.let {
                    PriceRow(
                        icon = Icons.Default.AccessTime,
                        label = label,
                        price = it.NOK_per_kWh,
                        time = it.getTimeRange()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.change_time_arrows),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Forrige time",
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clickable {
                                        if (hourIndex > 0) onHourChange(hourIndex - 1)
                                    },
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Neste time",
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clickable {
                                        if (hourIndex < prices.lastIndex) onHourChange(hourIndex + 1)
                                    },
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            highestPrice?.let {
                PriceRow(
                    icon = Icons.Default.ArrowUpward,
                    label = stringResource(R.string.highest_price),
                    price = it.NOK_per_kWh,
                    time = it.getTimeRange()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            lowestPrice?.let {
                PriceRow(
                    icon = Icons.Default.ArrowDownward,
                    label = stringResource(R.string.lowest_price),
                    price = it.NOK_per_kWh,
                    time = it.getTimeRange()
                )
            }
        }
    }
}

@Composable
fun PriceRow(
    icon: ImageVector?,
    label: String,
    price: Double,
    time: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (ThemeState.themeMode == ThemeMode.DARK) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "$label:",
                style = MaterialTheme.typography.titleLarge,
//                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraLight,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
//                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${"%2f".format(price)} NOK/kWh",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.time) + " $time",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun HomePriceCard(
    prices: List<ElectricityPrice>,
    selectedRegion: Region?,
) {
    Column {
        if (selectedRegion == null) {
            LoadingScreen()
            return
        }
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
//                .fillMaxSize()
            ,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val label = stringResource(R.string.price_now) + " ${selectedRegion?.name}"
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                PriceRow(
                    icon = null,
                    label = label,
                    price = it.NOK_per_kWh,
                    time = it.getTimeRange()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                ElectricityTowers()

            }
        }
    }
}
