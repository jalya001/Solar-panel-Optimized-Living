package no.solcellepanelerApp.ui.map

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.solcellepanelerApp.R
import no.solcellepanelerApp.model.electricity.Region
import no.solcellepanelerApp.ui.electricity.RegionDropdown
import no.solcellepanelerApp.ui.font.FontSizeState
import no.solcellepanelerApp.ui.navigation.InfoHelpButton
import no.solcellepanelerApp.ui.navigation.getCompassDirection
import no.solcellepanelerApp.ui.result.WeatherViewModel
import no.solcellepanelerApp.ui.reusables.DecimalFormatter
import no.solcellepanelerApp.ui.reusables.DecimalInputField

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
    selectedRegion: Region?,
    onRegionSelected: (Region) -> Unit,
) {
    if (!visible) return

    var angle by remember { mutableFloatStateOf(0f) }
    var areaState by remember { mutableStateOf(area) }
    var azimuthPosition by remember { mutableFloatStateOf(0f) }
    var efficiency by remember { mutableFloatStateOf(0f) }

    val panelTypes = remember {
        listOf(
            SolarPanelType("Monokrystallinsk", 20f, "Høy effektivitet, dyrere, Standard"),
            SolarPanelType("Polykrystallinsk", 15f, "Middels effektivitet, rimeligere"),
            SolarPanelType("Tynnfilm", 10f, "Lav effektivitet, fleksibel")
        )
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentDensity = LocalDensity.current
    val customDensity = Density(currentDensity.density, FontSizeState.fontScale.floatValue)

    LaunchedEffect(area) {
        areaState = area
    }

    CompositionLocalProvider(LocalDensity provides customDensity) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.tertiary,
            scrimColor = Color.Black.copy(alpha = 0.8f)
        ) {
            BottomSheetContent(
                areaState = areaState,
                onAreaChange = { areaState = it },
                onStartDrawing = onStartDrawing,
                angle = angle,
                onAngleChange = { angle = it },
                efficiency = efficiency,
                onEfficiencyChange = { efficiency = it },
                azimuth = azimuthPosition,
                onAzimuthChange = { azimuthPosition = it },
                panelTypes = panelTypes,
                selectedRegion = selectedRegion,
                onRegionSelected = onRegionSelected,
                coordinates = coordinates,
                height = height,
                onDismiss = onDismiss,
                viewModel = viewModel,
                weatherViewModel = weatherViewModel,
                navController = navController
            )
        }
    }
}

@Composable
private fun BottomSheetContent(
    areaState: String,
    onAreaChange: (String) -> Unit,
    onStartDrawing: () -> Unit,
    angle: Float,
    onAngleChange: (Float) -> Unit,
    efficiency: Float,
    onEfficiencyChange: (Float) -> Unit,
    azimuth: Float,
    onAzimuthChange: (Float) -> Unit,
    panelTypes: List<SolarPanelType>,
    selectedRegion: Region?,
    onRegionSelected: (Region) -> Unit,
    coordinates: Pair<Double, Double>?,
    height: Double?,
    onDismiss: () -> Unit,
    viewModel: MapScreenViewModel,
    weatherViewModel: WeatherViewModel,
    navController: NavController,
) {
    val focusManager = LocalFocusManager.current
    val uriHandler = LocalUriHandler.current
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
            fontWeight = FontWeight.Thin
        )

        Spacer(modifier = Modifier.height(20.dp))

        AreaInputRow(
            areaState,
            onAreaChange,
            onStartDrawing,
            onDismiss,
            viewModel = viewModel,

            decimalFormatter = decimalFormatter
        )
        Spacer(modifier = Modifier.height(8.dp))
        InfoHelpButton(stringResource(id = R.string.area_label), stringResource(id = R.string.roofAreaHelp))

        Spacer(modifier = Modifier.height(16.dp))
        SlopeSlider(angle, onAngleChange)
        InfoHelpButton("${stringResource(id = R.string.angle)} ${angle.toInt()}°", stringResource(id = R.string.roofAngleHelp))

        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(id = R.string.efficiency_label), style = MaterialTheme.typography.titleLarge)

        panelTypes.forEach { panelType ->
            PanelTypeCard(panelType, efficiency, onEfficiencyChange, viewModel, focusManager)
        }

        LearnMoreLink()
        InfoHelpButton("${stringResource(id = R.string.efficiency)} ~ ${efficiency.toInt()}%", stringResource(id = R.string.panelEfficencyHelp))

        Spacer(modifier = Modifier.height(16.dp))
        DirectionSlider(azimuth, onAzimuthChange)
        InfoHelpButton("${stringResource(id = R.string.direction)} ${azimuth.toInt()}° (${getCompassDirection(azimuth.toInt())})", stringResource(id = R.string.panelDirectionHelp))

        Spacer(modifier = Modifier.height(16.dp))
        selectedRegion?.let {
            RegionDropdown(it, onRegionSelected)
        }
        Spacer(modifier = Modifier.height(16.dp))
        ResultNavigationButton(
            areaState = areaState,
            coordinates = coordinates,
            angle = angle,
            efficiency = efficiency,
            azimuth = azimuth,
            height = height,
            navController = navController,
            viewModel = viewModel,
            weatherViewModel = weatherViewModel
        )
    }
}


data class SolarPanelType(
    val name: String,
    val efficiency: Float,
    val description: String
)

@Composable
fun AreaInputRow(
    areaState: String,
    onAreaChange: (String) -> Unit,
    onStartDrawing: () -> Unit,
    onDismiss: () -> Unit,
    decimalFormatter: DecimalFormatter,
    viewModel: MapScreenViewModel
) {
    Row {
        DecimalInputField(
            decimalFormatter = decimalFormatter,
            value = areaState,
            label = stringResource(id = R.string.area_label),
            onValueChange = onAreaChange,
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
        )
        Button(
            onClick = {
                //viewModel.drawingEnabled= true
                viewModel.startDrawing()
                //onStartDrawing()

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
}

@Composable
fun SlopeSlider(
    angle: Float,
    onAngleChange: (Float) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Text(
        text = stringResource(id = R.string.slope_label),
        style = MaterialTheme.typography.titleLarge
    )

    Slider(
        value = angle,
        onValueChange = {
            onAngleChange(it)
            focusManager.clearFocus()
        },
        valueRange = 0f..90f,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DirectionSlider(
    azimuth: Float,
    onAzimuthChange: (Float) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Text(
        text = stringResource(id = R.string.direction_label),
        style = MaterialTheme.typography.titleLarge
    )

    Slider(
        value = azimuth,
        onValueChange = {
            onAzimuthChange(it)
            focusManager.clearFocus()
        },
        valueRange = 0f..315f,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PanelTypeCard(
    panelType: SolarPanelType,
    selectedEfficiency: Float,
    onSelected: (Float) -> Unit,
    viewModel: MapScreenViewModel,
    focusManager: FocusManager
) {
    val selected = selectedEfficiency == panelType.efficiency
    val glowAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    val panelEff = when (panelType.name) {
        "Monokrystallinsk" -> "18-23"
        "Polykrystallinsk" -> "15-17"
        else -> "10-17"
    }

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
                elevation = if (selected) 6.dp else 3.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha)
            )
            .clickable {
                onSelected(panelType.efficiency)
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

@Composable
fun LearnMoreLink(
    url: String = "https://blogg.fusen.no/alle/ulike-typer-solcelleteknologi"
) {
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = Modifier
            .clickable { uriHandler.openUri(url) }
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Les mer om solcellepaneler",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = "Les mer",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ResultNavigationButton(
    areaState: String,
    coordinates: Pair<Double, Double>?,
    angle: Float,
    efficiency: Float,
    azimuth: Float,
    height: Double?,
    navController: NavController,
    viewModel: MapScreenViewModel,
    weatherViewModel: WeatherViewModel
) {
    if (areaState.isNotEmpty() && coordinates != null) {
        Button(
            onClick = {
                viewModel.areaInput = areaState
                viewModel.angleInput = angle.toString()
                viewModel.efficiencyInput = efficiency.toString()
                viewModel.directionInput = azimuth.toInt().toString()

                val (lat, lon) = coordinates

                weatherViewModel.loadWeatherData(
                    lat = lat,
                    lon = lon,
                    height = height,
                    slope = angle.toInt(),
                    azimuth = azimuth.toInt()
                )

                navController.navigate("result")
            },
            //modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Gå til resultater")
        }
    }
}
