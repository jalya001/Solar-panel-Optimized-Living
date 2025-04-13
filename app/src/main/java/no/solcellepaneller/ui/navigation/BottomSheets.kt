package no.solcellepaneller.ui.navigation

import android.graphics.drawable.Icon
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
import androidx.navigation.NavController
import no.solcellepaneller.ui.font.FontSizeState
import no.solcellepaneller.ui.language.langSwitch
import no.solcellepaneller.ui.map.MapScreenViewModel
import no.solcellepaneller.ui.font.FontScaleViewModel
import no.solcellepaneller.ui.theme.ThemeMode
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import no.solcellepaneller.ui.handling.DecimalFormatter
import no.solcellepaneller.ui.handling.DecimalInputField
import java.text.DecimalFormatSymbols

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    navController: NavController,
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
                    modifier = Modifier.size(width = 240.dp, height = 100.dp),
                    onClick ={navController.navigate("app_help") }

                ) {
                    Text(
                        stringResource(id = R.string.help_how),
                        modifier = Modifier
                            .padding(16.dp),
                        textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall
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
                    modifier = Modifier.size(width = 240.dp, height = 100.dp),
                    onClick ={navController.navigate("tech_help")}
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
                    Text(stringResource(id = R.string.close), style = MaterialTheme.typography.bodySmall)
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
    fontScaleViewModel: FontScaleViewModel
) {
    if (visible) {
        val currentDensity = LocalDensity.current
        val customDensity = Density(
            density = currentDensity.density,
            fontScale = FontSizeState.fontScale.value
        )

        ModalBottomSheet(
            onDismissRequest = onDismiss,
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

                    if(!followSystem) {
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
                                    .align(Alignment.CenterHorizontally)
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
    viewModel: MapScreenViewModel
) {
    var angle by remember { mutableStateOf("") }
    var areaState by remember { mutableStateOf(area) }

    LaunchedEffect(area) {
        areaState = area
    }

    var direction by remember { mutableStateOf("") }
    val directions = listOf("North", "East", "South", "West")
    val azimuthValues = listOf("0", "90", "180", "270")
    var selectedIndex by remember { mutableStateOf(-1) } // nothing selected yet
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
                        .heightIn(max = 800.dp)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Button(
                            onClick = {
                                areaState = "45"
                                angle = "30"
                                efficiency = "85"
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Fyll ut testverdier")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = stringResource(id = R.string.area_label),
                            style = MaterialTheme.typography.labelLarge
                        )

                        val decimalFormatter = DecimalFormatter()

                        Row {
                            DecimalInputField(
                                decimalFormatter = decimalFormatter,
                                value = areaState,
                                label = stringResource(id = R.string.area_label),
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
                                    Text(stringResource(id = R.string.draw_area))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(stringResource(id = R.string.slope_label), style = MaterialTheme.typography.labelLarge)
                        DecimalInputField(
                            onValueChange = { angle = it },
                            label = stringResource(id = R.string.slope_label),
                            modifier = Modifier.fillMaxWidth(),
                            value = angle,
                            decimalFormatter = decimalFormatter
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(stringResource(id = R.string.efficiency_label), style = MaterialTheme.typography.labelLarge)
                        DecimalInputField(
                            onValueChange = { efficiency = it },
                            label = stringResource(id = R.string.efficiency_label),
                            modifier = Modifier.fillMaxWidth(),
                            value = efficiency,
                            decimalFormatter = decimalFormatter
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(stringResource(id = R.string.direction_label), style = MaterialTheme.typography.labelLarge)
                        Box {
                            Button(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (selectedIndex >= 0)
                                        "(${azimuthValues[selectedIndex]}°)"
                                    else
                                        "Choose direction (°)"
                                )

                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown arrow"
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                directions.forEachIndexed { index, dir ->
                                    DropdownMenuItem(
                                        text = { Text("$dir (${azimuthValues[index]}°)") },
                                        onClick = {
                                            selectedIndex = index
                                            direction = azimuthValues[index]
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (areaState.isNotEmpty() && angle.isNotEmpty() && efficiency.isNotEmpty() && direction.isNotEmpty() && coordinates != null) {
                            Button(
                                onClick = {
                                    viewModel.areaInput = areaState
                                    viewModel.angleInput = angle
                                    viewModel.efficiencyInput = efficiency
                                    viewModel.directionInput = direction
                                    navController.navigate("result")
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(stringResource(id = R.string.go_to_results))
                            }
                        }
                    }
                }
            }
        }
    }
}