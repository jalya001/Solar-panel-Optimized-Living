package no.solcellepanelerApp.ui.navigation

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import no.solcellepanelerApp.R
import no.solcellepanelerApp.model.electricity.Region
import no.solcellepanelerApp.ui.electricity.RegionDropdown
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.font.FontSizeState
import no.solcellepanelerApp.ui.language.langSwitch
import no.solcellepanelerApp.ui.map.MapScreenViewModel
import no.solcellepanelerApp.ui.result.WeatherViewModel
import no.solcellepanelerApp.ui.reusables.DecimalFormatter
import no.solcellepanelerApp.ui.reusables.DecimalInputField
import no.solcellepanelerApp.ui.reusables.ExpandInfoSection
import no.solcellepanelerApp.ui.reusables.ModeCard
import no.solcellepanelerApp.ui.reusables.MySection
import no.solcellepanelerApp.ui.theme.ThemeMode
import no.solcellepanelerApp.ui.theme.ThemeState
import no.solcellepanelerApp.util.RememberLocationWithPermission


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    expandSection: String = "",
    navController: NavController,
) {

    var triggerLocationFetch by remember { mutableStateOf(false) }

    var region: Region? by remember { mutableStateOf(null) }
    val (currentLocation, locationGranted) = if (triggerLocationFetch) {
        RememberLocationWithPermission(
            triggerRequest = true,
            onRegionDetermined = { region = it }
        )
    } else {
        Pair(null, false)
    }


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
                        val context = LocalContext.current
                        val locationGranted = ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED

                        var showDialog by remember { mutableStateOf(false) }

                        if (showDialog) {
                            androidx.compose.material3.AlertDialog(
                                onDismissRequest = { showDialog = false },
                                title = { Text("Location Permission Already Granted") },
                                text = { Text("If you want to change location permissions, go to settings.") },
                                confirmButton = {
                                    Button(onClick = {
                                        val intent =
                                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                data = Uri.fromParts(
                                                    "package",
                                                    context.packageName,
                                                    null
                                                )
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                        context.startActivity(intent)
                                        showDialog = false
                                    }) {
                                        Text("Go to Settings")
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = { showDialog = false }) {
                                        Text("Cancel")
                                    }
                                }
                            )
                        }
                        MySection(
                            title = if (locationGranted) "Change Location Settings" else "Grant Location Access",
                            onClick = {
//                                if (locationGranted) {
//                                    val intent =
//                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                                            data =
//                                                Uri.fromParts("package", context.packageName, null)
//                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                        }
//                                    context.startActivity(intent)
//                                }

                                if (locationGranted) {
                                    showDialog = true
                                } else {
                                    triggerLocationFetch = true
                                }
                            },
                            iconRes = R.drawable.baseline_my_location_24
                        )
                    }


                    item {
                        MySection(
                            title = "Open Tutorial",
                            onClick = {
                                navController.navigate("onboarding")
                            },
                            iconRes = R.drawable.school_24px
                        )
                    }
                    item {
                        ExpandInfoSection(
                            title = stringResource(id = R.string.help_draw),
                            content = stringResource(id = R.string.how_to_draw),
                            initiallyExpanded = expandSection == "draw"
                        )
                    }
                    item {
                        ExpandInfoSection(
//                            title = stringResource(id = R.string.tech_problems_title),
                            title = "*Noe noe*",
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

@Composable
fun getCompassDirection(degree: Int): String {
    return when (degree) {
        in 0..22 -> stringResource(id = R.string.north)
        in 23..67 -> stringResource(id = R.string.northeast)
        in 68..112 -> stringResource(id = R.string.east)
        in 113..157 -> stringResource(id = R.string.southeast)
        in 158..202 -> stringResource(id = R.string.south)
        in 203..247 -> stringResource(id = R.string.southwest)
        in 248..292 -> stringResource(id = R.string.west)
        else -> stringResource(id = R.string.northwest)
    }
}

//Hjelpe funksjon for å formattere hjelpe knapp og hjelp info
@Composable
fun InfoHelpButton(
    label: String,
    helpText: String,
) {
    var isVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = { isVisible = !isVisible },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        if (isVisible) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(helpText, style = MaterialTheme.typography.bodyMedium)
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
    height: Double?,
    area: String,
    navController: NavController,
    viewModel: MapScreenViewModel,
    weatherViewModel: WeatherViewModel,
) {
    var angle by remember { mutableStateOf(0f) }
    var areaState by remember { mutableStateOf(area) }
    var azimuthPosition by remember { mutableStateOf(0f) }
    var efficiency by remember { mutableStateOf(0f) }

    val context = LocalContext.current
    val activity = context as? Activity

    data class SolarPanelType(val name: String, val efficiency: Float, val description: String)

    val panelTypes = listOf(
        SolarPanelType("Monokrystallinsk", 20f, "Høy effektivitet, dyrere, Standard"),
        SolarPanelType("Polykrystallinsk", 15f, "Middels effektivitet, rimeligere"),
        SolarPanelType("Tynnfilm", 10f, "Lav effektivitet, fleksibel")
    )

    LaunchedEffect(area) {
        areaState = area
    }

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
                val focusManager = LocalFocusManager.current
                val decimalFormatter = DecimalFormatter()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .padding(bottom = 150.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.additional_input),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Thin,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

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
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    InfoHelpButton(
                        label = stringResource(id = R.string.area_label),
                        helpText = stringResource(id = R.string.roofAreaHelp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(id = R.string.slope_label),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Slider(
                        value = angle,
                        onValueChange = {
                            angle = it
                            focusManager.clearFocus()
                        },
                        valueRange = 0f..90f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    InfoHelpButton(
                        label = stringResource(id = R.string.angle) + " ${angle.toInt()}°",
                        helpText = stringResource(id = R.string.roofAngleHelp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        stringResource(id = R.string.efficiency_label),
                        style = MaterialTheme.typography.titleLarge
                    )

//                    Slider(
//                        value = efficiency,
//                        onValueChange = {
//                            efficiency = it
//                            focusManager.clearFocus()
//                        },
//                        valueRange = 0f..100f,
//                        modifier = Modifier.fillMaxWidth()
//                    )


                    panelTypes.forEach { panelType ->
                        val selected = efficiency == panelType.efficiency
                        val glowAlpha by animateFloatAsState(
                            targetValue = if (selected) 1f else 0f,
                            animationSpec = tween(durationMillis = 500)
                        )

                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .border(
                                    width = if (selected) 3.dp else 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .shadow(
                                    elevation = if (selected) 8.dp else 12.dp,
                                    shape = RoundedCornerShape(12.dp),
                                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha)
                                )
                                .clickable {
                                    efficiency = panelType.efficiency
                                    viewModel.efficiencyInput = panelType.efficiency.toString()
                                    focusManager.clearFocus()
                                },
                            colors = CardDefaults.cardColors(
                                contentColor = if (selected)
                                    MaterialTheme.colorScheme.surface
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                var panelEff = if (panelType.name == "Monokrystallinsk") {
                                    "18-23"
                                } else if (panelType.name == "Polykrystallinsk") {
                                    "15-17"
                                } else {
                                    "10-17"
                                }





                                Text(
                                    text = "${panelType.name} (${panelEff} %)",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = panelType.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    val uriHandler = LocalUriHandler.current
                    Row(
                        modifier = Modifier
                            .clickable { uriHandler.openUri("https://blogg.fusen.no/alle/ulike-typer-solcelleteknologi") }
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Les mer om solcellepaneler",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { uriHandler.openUri("https://blogg.fusen.no/alle/ulike-typer-solcelleteknologi") }
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Les mer",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    InfoHelpButton(
                        label = stringResource(id = R.string.efficiency) + " ~${efficiency.toInt()}%",
                        helpText = stringResource(id = R.string.panelEfficencyHelp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(id = R.string.direction_label),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Slider(
                        value = azimuthPosition,
                        onValueChange = {
                            azimuthPosition = it
                            focusManager.clearFocus()
                        },
                        valueRange = 0f..315f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    InfoHelpButton(
                        label = stringResource(id = R.string.direction) + " ${azimuthPosition.toInt()}° (${
                            getCompassDirection(
                                azimuthPosition.toInt()
                            )
                        })",
                        helpText = stringResource(id = R.string.panelDirectionHelp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    var selectedRegion by remember { mutableStateOf(Region.OSLO) }
                    RegionDropdown(
                        selectedRegion = viewModel.selectedRegion,
                        onRegionSelected = { newRegion ->
                            viewModel.selectedRegion = newRegion
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (areaState.isNotEmpty() && coordinates != null) {
                        Button(
                            onClick = {
                                viewModel.areaInput = areaState
                                viewModel.angleInput = angle.toString()
                                viewModel.efficiencyInput = efficiency.toString()
                                viewModel.directionInput = azimuthPosition.toInt().toString()

                                val lat = coordinates.first
                                val lon = coordinates.second
                                val slope = angle

                                weatherViewModel.loadWeatherData(
                                    lat, lon, height,
                                    slope.toInt(),
                                    azimuthPosition.toInt()
                                )

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


