package no.solcellepaneller.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.solcellepaneller.R
import no.solcellepaneller.ui.font.FontScaleViewModel
import no.solcellepaneller.ui.font.FontSizeState
import no.solcellepaneller.ui.language.langSwitch
import no.solcellepaneller.ui.map.MapScreenViewModel
import no.solcellepaneller.ui.result.WeatherViewModel
import no.solcellepaneller.ui.reusables.DecimalFormatter
import no.solcellepaneller.ui.reusables.DecimalInputField
import no.solcellepaneller.ui.reusables.ExpandInfoSection
import no.solcellepaneller.ui.theme.ThemeMode
import no.solcellepaneller.ui.theme.ThemeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    expandSection: String = "",
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.tertiary,
            scrimColor = Color.Black.copy(alpha = 0.8f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(id = R.string.help),
                    style = MaterialTheme.typography.titleLarge
                )

                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    item {
                        ExpandInfoSection(
                            title = stringResource(id = R.string.help_draw),

                            content = stringResource(id = R.string.how_to_draw),
                            initiallyExpanded = expandSection == "draw"
                        )
                    }
                    item {
                        ExpandInfoSection(
                            title = stringResource(id = R.string.tech_problems_title),
                            content = stringResource(id = R.string.tech_problems_content)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                ) {
                    Text(
                        stringResource(id = R.string.close),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    fontScaleViewModel: FontScaleViewModel,
) {
    if (visible) {
        val currentDensity = LocalDensity.current
        val customDensity = Density(
            density = currentDensity.density,
            fontScale = FontSizeState.fontScale.value
        )

        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.tertiary,
            scrimColor = Color.Black.copy(alpha = 0.8f)
        ) {
            CompositionLocalProvider(LocalDensity provides customDensity) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(id = R.string.appereance),
                        style = MaterialTheme.typography.titleLarge
                    )

                    var followSystem by remember { mutableStateOf(ThemeState.themeMode == ThemeMode.SYSTEM) }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = followSystem,
                            onCheckedChange = { checked ->
                                followSystem = checked
                                ThemeState.themeMode = if (checked) ThemeMode.SYSTEM
                                else ThemeMode.LIGHT
                            }
                        )
                        Text(stringResource(id = R.string.follow_system))
                    }

                    if (!followSystem) {
                        ElevatedCard(
                            colors = CardDefaults.elevatedCardColors(
                                contentColor = MaterialTheme.colorScheme.tertiary,
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.size(width = 240.dp, height = 60.dp),
                            onClick = {
                                ThemeState.themeMode = when (ThemeState.themeMode) {
                                    ThemeMode.LIGHT -> ThemeMode.DARK
                                    ThemeMode.DARK -> ThemeMode.LIGHT
                                    ThemeMode.SYSTEM -> ThemeMode.LIGHT
                                }
                            },
                            enabled = !followSystem
                        ) {
                            val text = when (ThemeState.themeMode) {
                                ThemeMode.LIGHT -> stringResource(id = R.string.dark_mode)
                                ThemeMode.DARK -> stringResource(id = R.string.light_mode)
                                ThemeMode.SYSTEM -> stringResource(id = R.string.dark_mode)
                            }

                            Text(
                                text = text,
                                modifier = Modifier
                                    .padding(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    langSwitch()

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = {
                            fontScaleViewModel.decreaseFontScale()
                        }) {
                            Text("- A", style = MaterialTheme.typography.bodySmall)
                        }
                        Button(onClick = {
                            fontScaleViewModel.resetFontScale()
                        }) {
                            Text("Reset", style = MaterialTheme.typography.bodySmall)
                        }
                        Button(onClick = {
                            fontScaleViewModel.increaseFontScale()
                        }) {
                            Text("+ A", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onDismiss,
                    ) {
                        Text(
                            stringResource(id = R.string.close),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
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
    viewModel: MapScreenViewModel,
    weatherViewModel: WeatherViewModel,
) {
    var angle by remember { mutableStateOf("") }
    var areaState by remember { mutableStateOf(area) }

    LaunchedEffect(area) {
        areaState = area
    }

    var direction by remember { mutableStateOf("") }
    var efficiency by remember { mutableStateOf("") }

    if (visible) {
        val currentDensity = LocalDensity.current
        val customDensity = Density(
            density = currentDensity.density,
            fontScale = FontSizeState.fontScale.value
        )

        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
        CompositionLocalProvider(LocalDensity provides customDensity) {
            ModalBottomSheet(
                onDismissRequest = onDismiss,
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.tertiary,
                scrimColor = Color.Black.copy(alpha = 0.8f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(id = R.string.additional_input),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = Bold
                    )
                    Button(
                        onClick = {
                            areaState = "45"
                            angle = "30"
                            direction = "1"
                            efficiency = "85"
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Fyll ut testverdier")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val decimalFormatter = DecimalFormatter()

                    Row {
                        DecimalInputField(
                            decimalFormatter = decimalFormatter,
                            value = areaState,
                            label = stringResource(id = R.string.area_label),
                            onValueChange = { areaState = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp)
                        )

                        Button(
                            onClick = {
                                onStartDrawing()
                                onDismiss()
                            },
                            modifier = Modifier.height(70.dp)
                        ) {
                            Row {
                                Icon(
                                    imageVector = Icons.Filled.Create,
                                    contentDescription = stringResource(id = R.string.draw_area),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    stringResource(id = R.string.draw_area),
                                    style = MaterialTheme.typography.bodyMedium
                                ) //Kanksje lurt å legge til noe som hindrer bruker i å klikkevekk, men samtidig vil vi at de skal kunne bytte posisjon hvis ufornøyde
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    DecimalInputField(
                        onValueChange = { angle = it },
                        label = stringResource(id = R.string.slope_label),
                        modifier = Modifier.fillMaxWidth(),
                        value = angle,
                        decimalFormatter = decimalFormatter
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    DecimalInputField(
                        onValueChange = { direction = it },
                        label = stringResource(id = R.string.direction_label),
                        modifier = Modifier.fillMaxWidth(),
                        value = direction,
                        decimalFormatter = decimalFormatter
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    DecimalInputField(
                        onValueChange = { efficiency = it },
                        label = stringResource(id = R.string.efficiency_label),
                        modifier = Modifier.fillMaxWidth(),
                        value = efficiency,
                        decimalFormatter = decimalFormatter
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (areaState.isNotEmpty() && direction.isNotEmpty() && angle.isNotEmpty() && efficiency.isNotEmpty() && coordinates != null) {
                        Button(
                            onClick = {
                                viewModel.areaInput = areaState
                                viewModel.angleInput = angle
                                viewModel.efficiencyInput = efficiency
                                viewModel.directionInput = direction

                                val lat = coordinates.first
                                val lon = coordinates.second
                                val slope = angle.toIntOrNull()

                                weatherViewModel.fetchFrostData(
                                    lat, lon,
                                    listOf(
                                        "mean(snow_coverage_type P1M)",
                                        "mean(air_temperature P1M)",
                                        "mean(cloud_area_fraction P1M)"
                                    )
                                )


                                if (slope != null) {
                                    weatherViewModel.fetchRadiationInfo(lat, lon, slope)
                                }
                                navController.navigate("result")

                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Gå til resultater")
                        }
                    }

                }
            }
        }
    }
}