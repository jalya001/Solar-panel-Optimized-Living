package no.solcellepaneller.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import no.solcellepaneller.ui.theme.ThemeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpBottomSheet( //Kan enten velge å ha alt info her eller igjen navigate herfra til videre screeen
    visible: Boolean,
    onDismiss: () -> Unit
) {
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.tertiary,
            scrimColor = Color.Black.copy(alpha = 0.8f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Hjelp", style = MaterialTheme.typography.titleLarge)

                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                    contentColor = MaterialTheme.colorScheme.tertiary,
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    modifier = Modifier.size(width = 240.dp, height = 100.dp)
                ) {
                    Text(
                        text = "Help (How to use the app)",
                        modifier = Modifier
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    modifier = Modifier.size(width = 240.dp, height = 100.dp)
                ) {
                    Text(
                        text = "Help (Technical help, Problems with the app)",
                        modifier = Modifier
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                ) {
                    Text("Lukk")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.tertiary,
            scrimColor = Color.Black.copy(alpha = 0.8f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), horizontalAlignment=Alignment.CenterHorizontally
            ) {
                Text("Informasjon", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Information about the electricity prices and what the pros and cons of installing solar panels in Norway is.  Should also inform about the savings in electricity bills. Maybe also some information about how the electricity prices, savings and the electricity production (in kWh) is calculated (in general).Also information about expected power production on a off-grid cabin. Further information about (almost) anything relevant for the user to know before deciding to install solar panels.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                ) {
                    Text("Lukk")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.tertiary,
            scrimColor = Color.Black.copy(alpha = 0.8f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Utseende", style = MaterialTheme.typography.titleLarge)

                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    modifier = Modifier.size(width = 240.dp, height = 60.dp),
                    onClick = {ThemeState.isDark = !ThemeState.isDark}
                ) {
                    Text(
                        text = if (ThemeState.isDark) "Bytt til lysmodus" else "Bytt til mørkmodus",
                        modifier = Modifier
                            .padding(16.dp).align(Alignment.CenterHorizontally),
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                    contentColor = MaterialTheme.colorScheme.tertiary,
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    modifier = Modifier.size(width = 240.dp, height = 60.dp),
                ) {
                    Text(
                        text = "Bytt til engelsk",
                        modifier = Modifier
                            .padding(16.dp).align(Alignment.CenterHorizontally),
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    modifier = Modifier.size(width = 240.dp, height = 60.dp),
                ) {
                    Text(
                        text = "Bytt skriftstørrelse",
                        modifier = Modifier
                            .padding(16.dp).align(Alignment.CenterHorizontally),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                ) {
                    Text("Lukk")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionalInputBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    navController: NavController,
    coordinates: Pair<Double, Double>?,
    ) {
    var angle by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf("") }
    var efficiency by remember { mutableStateOf("") }

    val currentCoordinates by rememberUpdatedState(newValue = coordinates)

    val closestWeatherStation = "Filler"

    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.tertiary,
            scrimColor = Color.Black.copy(alpha = 0.8f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Button(
                    onClick = {navController.navigate("map") }
                ) {
                    Text(text = "Fyll ut automatisk ved å tegne området")
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(thickness = 3.dp)
                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Area", style = MaterialTheme.typography.labelLarge)
                TextField(
                    value = area,
                    onValueChange = { area = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Angle:", style = MaterialTheme.typography.labelLarge)
                TextField(
                    value = angle,
                    onValueChange = { angle = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Directions:", style = MaterialTheme.typography.labelLarge)
                TextField(
                    value = direction,
                    onValueChange = { direction = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Efficiency", style = MaterialTheme.typography.labelLarge)
                TextField(
                    value = efficiency,
                    onValueChange = { efficiency = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Map koordinater:", style = MaterialTheme.typography.labelLarge)
                if (currentCoordinates != null) {
                    Text(text = "Lat: ${currentCoordinates!!.first}, Lon: ${currentCoordinates!!.second}")
                } else {
                    Text(text = "Ingen koordinater valgt")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Nærmeste værstasjon:", style = MaterialTheme.typography.labelLarge)
                Text(text = closestWeatherStation)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Close")
                }
            }
        }
    }
}
