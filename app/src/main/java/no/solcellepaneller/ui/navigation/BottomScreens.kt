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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import no.solcellepaneller.R
import no.solcellepaneller.ui.theme.ThemeState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import no.solcellepaneller.ui.map.LocationNotSelectedDialog
import no.solcellepaneller.ui.map.MapScreenViewModel

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
                Text(stringResource(id = R.string.help), style = MaterialTheme.typography.titleLarge)

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
                        stringResource(id = R.string.help_how),
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
                        stringResource(id = R.string.help_techinical),
                        modifier = Modifier
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                ) {
                    Text(stringResource(id = R.string.close))
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
                Text(stringResource(id = R.string.appereance), style = MaterialTheme.typography.titleLarge)

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
                        text = if (ThemeState.isDark) stringResource(id = R.string.light_mode) else stringResource(id = R.string.dark_mode),
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
                        stringResource(id = R.string.language),
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
                        stringResource(id = R.string.font_size),
                        modifier = Modifier
                            .padding(16.dp).align(Alignment.CenterHorizontally),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                ) {
                    Text(stringResource(id = R.string.close))
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
    onStartDrawing: () -> Unit,
    coordinates: Pair<Double, Double>?,
    area: String,
    navController: NavController,
    viewModel: MapScreenViewModel
) {
    var angle by remember { mutableStateOf("") }
//    var area by remember { mutableStateOf("") }
    var areaState by remember { mutableStateOf(area) }

    LaunchedEffect(area) {
        areaState = area
    }

    var direction by remember { mutableStateOf("") }
    var efficiency by remember { mutableStateOf("") }

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
                Text(stringResource(id = R.string.coordinates_label), style = MaterialTheme.typography.labelLarge)
                Text("Lat: ${coordinates?.first}, Lon: ${coordinates?.second}")


                Spacer(modifier = Modifier.height(10.dp))
                
                Text("Areal (m²)", style = MaterialTheme.typography.labelLarge)

                Row {
                    TextField(
                        value = areaState,
                        onValueChange = { areaState = it }
                    )
                    Button(
                        onClick = {
                            onStartDrawing()
                            onDismiss()
                        }
                    ) {
                        Column {
                            Icon(
                                imageVector = Icons.Filled.Create,
                                contentDescription = stringResource(id = R.string.draw_area),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(stringResource(id = R.string.draw_area)) //Kanksje lurt å legge til noe som hindrer bruker i å klikkevekk, men samtidig vil vi at de skal kunne bytte posisjon hvis ufornøyde
                        }
//                        Text("Fyll ut area ved å tegne området")

                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(stringResource(id = R.string.slope_label), style = MaterialTheme.typography.labelLarge)
                TextField(
                    label = { Text(stringResource(id = R.string.slope_label)) },
                    value = angle,
                    onValueChange = { angle = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(stringResource(id = R.string.direction_label), style = MaterialTheme.typography.labelLarge)
                TextField(
                    label = { Text(stringResource(id = R.string.direction_label)) },
                    value = direction,
                    onValueChange = { direction = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(stringResource(id = R.string.efficiency_label), style = MaterialTheme.typography.labelLarge)
                TextField(
                    label = { Text(stringResource(id = R.string.efficiency_label)) },
                    value = efficiency,
                    onValueChange = { efficiency = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(stringResource(id = R.string.closest_weather_station), style = MaterialTheme.typography.labelLarge)
                Text(closestWeatherStation)

                Spacer(modifier = Modifier.height(16.dp))

                if (areaState.isNotEmpty() && direction.isNotEmpty() && angle.isNotEmpty() && efficiency.isNotEmpty() && coordinates != null) {
                    Button(
                        onClick = {
                            viewModel.areaInput = areaState
                            viewModel.angleInput = angle
                            viewModel.directionInput = direction
                            viewModel.efficiencyInput = efficiency
                            navController.navigate("result")
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Gå til resultater")
                    }
                }

            }        }
    }
}

//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "DefaultPreviewDark"
//)
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    name = "DefaultPreviewLight"
//)
//@Composable
//    fun AppPreview() {
//    SolcellepanellerTheme {
//        App()
//    }
//}