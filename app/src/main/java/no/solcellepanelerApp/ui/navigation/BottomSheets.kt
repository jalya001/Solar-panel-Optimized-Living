package no.solcellepanelerApp.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import no.solcellepanelerApp.R
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.font.FontSizeState
import no.solcellepanelerApp.ui.language.langSwitch
import no.solcellepanelerApp.ui.map.MapScreenViewModel
import no.solcellepanelerApp.ui.result.WeatherViewModel
import no.solcellepanelerApp.ui.reusables.DecimalFormatter
import no.solcellepanelerApp.ui.reusables.DecimalInputField
import no.solcellepanelerApp.ui.reusables.ExpandInfoSection
import no.solcellepanelerApp.ui.reusables.ModeCard
import no.solcellepanelerApp.ui.theme.ThemeMode
import no.solcellepanelerApp.ui.theme.ThemeState
import no.solcellepanelerApp.model.electricity.Region
import no.solcellepanelerApp.ui.electricity.RegionDropdown
import no.solcellepanelerApp.ui.language.langSwitch
import no.solcellepanelerApp.ui.reusables.DecimalInputField
import no.solcellepanelerApp.ui.reusables.ExpandInfoSection
import no.solcellepanelerApp.ui.reusables.ModeCard


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
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Thin
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
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(id = R.string.appearance),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Thin
                    )

                    var followSystem by remember { mutableStateOf(ThemeState.themeMode == ThemeMode.SYSTEM) }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ModeCard(
                            label = stringResource(id = R.string.light_mode),
                            iconRes = R.drawable.light_mode_24px,
                            selected = ThemeState.themeMode == ThemeMode.LIGHT && !followSystem,
                            onClick = {
                                followSystem = false
                                ThemeState.themeMode = ThemeMode.LIGHT
                            }
                        )

                        ModeCard(
                            label = stringResource(id = R.string.dark_mode),
                            iconRes = R.drawable.dark_mode_24px,
                            selected = ThemeState.themeMode == ThemeMode.DARK && !followSystem,
                            onClick = {
                                followSystem = false
                                ThemeState.themeMode = ThemeMode.DARK
                            }
                        )

                        ModeCard(
                            label = stringResource(id = R.string.frost_mode),
                            iconRes = R.drawable.outline_mode_cool_24,
                            selected = ThemeState.themeMode == ThemeMode.FROST && !followSystem,
                            onClick = {
                                followSystem = false
                                ThemeState.themeMode = ThemeMode.FROST
                            }
                        )
                    }

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

                    Spacer(modifier = Modifier.height(10.dp))

                    langSwitch()

                    Spacer(modifier = Modifier.height(10.dp))

                    //for max/min font
                    val snackbarHostState = remember { SnackbarHostState() }
                    var snackBarJob by remember { mutableStateOf<Job?>(null) }
                    val coroutineScope = rememberCoroutineScope()

                    SnackbarHost(hostState = snackbarHostState) //hvor snackbaren skal vises


                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = {

                            val didDecrease = fontScaleViewModel.decreaseFontScale()
                            if (!didDecrease && snackBarJob?.isActive != true) {
                                snackBarJob = coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Min font size reached")
                                }
                            }

                        }) {
                            Text("- A", style = MaterialTheme.typography.bodySmall)
                        }

                        Button(onClick = {
                            fontScaleViewModel.resetFontScale()
                        }) {
                            Text("Reset", style = MaterialTheme.typography.bodySmall)
                        }

                        Button(onClick = {

                            val didIncrease = fontScaleViewModel.increaseFontScale()
                            if (!didIncrease && snackBarJob?.isActive != true) {
                                snackBarJob = coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Max font size reached")
                                }
                            }

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
    val azimuthValues = listOf("0", "90", "180", "270")
    var azimuthInput by remember { mutableStateOf("") } // nothing selected yet
    var expanded by remember { mutableStateOf(false) }
    var efficiency by remember { mutableStateOf("") }

    if (visible) {
        val currentDensity = LocalDensity.current
        val customDensity = Density(
            density = currentDensity.density,
            fontScale = FontSizeState.fontScale.value
        )

        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )


        CompositionLocalProvider(LocalDensity provides customDensity) {
            ModalBottomSheet(
                onDismissRequest = onDismiss,
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.tertiary,
                scrimColor = Color.Black.copy(alpha = 0.8f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 600.dp)
                        .padding(16.dp)
                ) {

                }
                Column(
                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(id = R.string.additional_input),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Thin,

                        )
                    Button(
                        onClick = {
                            areaState = "45"
                            angle = "30"
                            direction = azimuthValues[1]
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

                    DecimalInputField(
                        onValueChange = { efficiency = it },
                        label = stringResource(id = R.string.efficiency_label),
                        modifier = Modifier.fillMaxWidth(),
                        value = efficiency,
                        decimalFormatter = decimalFormatter
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = azimuthInput,
                            onValueChange = { it ->
                                if (it.isEmpty() || (it.all { it.isDigit() } && it.toInt() in 0..359)) {
                                    azimuthInput = it
                                }

                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            label = { Text(stringResource(id = R.string.direction_label)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            azimuthValues.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text("$item°") },
                                    onClick = {
                                        azimuthInput = item
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(10.dp))

                    var selectedRegion by remember { mutableStateOf(Region.OSLO) }
                    RegionDropdown(
                        selectedRegion = viewModel.selectedRegion,
                        onRegionSelected = { newRegion ->
                            viewModel.selectedRegion = newRegion
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

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
                                    weatherViewModel.fetchRadiationInfo(
                                        lat,
                                        lon,
                                        slope,
                                        direction.toInt()
                                    )
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