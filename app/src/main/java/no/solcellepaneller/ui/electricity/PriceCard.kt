package no.solcellepaneller.ui.electricity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import no.solcellepaneller.model.electricity.ElectricityPrice
import no.solcellepaneller.ui.theme.ThemeState

@Composable
fun PriceCard(
    currentPrice: ElectricityPrice?,
    highestPrice: ElectricityPrice?,
    lowestPrice: ElectricityPrice?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
            Spacer(modifier = Modifier.height(8.dp))
            highestPrice?.let {
                PriceRow(
                    icon = Icons.Default.ArrowUpward,
                    label = "Høyeste pris",
                    price = it.NOK_per_kWh,
                    time = it.getTimeRange()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
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
    time: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (ThemeState.isDark) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = "$label: ${"%2f".format(price)} NOK/kWh",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Tid: $time",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}