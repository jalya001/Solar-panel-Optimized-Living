package no.solcellepaneller.ui.reusables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MyCard(
    text: String,
    modifier: Modifier = Modifier,
    size: DpSize = DpSize(width = 240.dp, height = 100.dp),
    style: String
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            contentColor = MaterialTheme.colorScheme.tertiary,
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier.padding(8.dp).size(size),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
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

@Composable
fun MyNavCard(
    text: String,
    route: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    size: DpSize = DpSize(width = 240.dp, height = 100.dp),
    style: String
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
