package no.solcellepaneller.ui.reusables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExpandInfoSection(title: String, content: String, initiallyExpanded: Boolean = false) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Card(
        colors = CardDefaults.elevatedCardColors(
            contentColor = MaterialTheme.colorScheme.tertiary,
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        onClick = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title, style = MaterialTheme.typography.titleMedium
            )

            if (expanded) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
