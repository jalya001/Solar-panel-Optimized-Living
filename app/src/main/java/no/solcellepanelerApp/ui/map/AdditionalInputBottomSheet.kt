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
import androidx.compose.runtime.collectAsState
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
import no.solcellepanelerApp.ui.reusables.DecimalFormatter
import no.solcellepanelerApp.ui.reusables.DecimalInputField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionalInputBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    navController: NavController,
    viewModel: MapViewModel,
) {
    if (!visible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentDensity = LocalDensity.current
    val customDensity = Density(currentDensity.density, FontSizeState.fontScale.floatValue)

    CompositionLocalProvider(LocalDensity provides customDensity) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.tertiary,
            scrimColor = Color.Black.copy(alpha = 0.8f)
        ) {
            BottomSheetContent(
                onDismiss = onDismiss,
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}

@Composable
private fun BottomSheetContent(
    onDismiss: () -> Unit,
    viewModel: MapViewModel,
    navController: NavController,
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
            fontWeight = FontWeight.Thin
        )

        Spacer(modifier = Modifier.height(20.dp))

        AreaInputRow(
            onDismiss,
            viewModel = viewModel,
            decimalFormatter = decimalFormatter
        )
        Spacer(modifier = Modifier.height(8.dp))
        InfoHelpButton(stringResource(id = R.string.area_label), stringResource(id = R.string.roofAreaHelp))

        Spacer(modifier = Modifier.height(16.dp))
        SlopeSlider(viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        PanelPicker(viewModel, focusManager)

        Spacer(modifier = Modifier.height(16.dp))
        DirectionSlider(viewModel)

        Spacer(modifier = Modifier.height(16.dp))
        //selectedRegion?.let {
        //    RegionDropdown(it, onRegionSelected)
        //}
        Spacer(modifier = Modifier.height(16.dp))
        ResultNavigationButton(
            navController = navController,
            viewModel = viewModel,
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
    onDismiss: () -> Unit,
    decimalFormatter: DecimalFormatter,
    viewModel: MapViewModel
) {
    val area by viewModel.areaState.stateFlow.collectAsState()

    Row {
        DecimalInputField(
            decimalFormatter = decimalFormatter,
            value = area.toString(),
            label = stringResource(id = R.string.area_label),
            onValueChange = { viewModel.areaState.value = it.toDouble() },
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
        )
        Button(
            onClick = {
                viewModel.startDrawing()
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
    viewModel: MapViewModel
) {
    val angle by viewModel.angleState.stateFlow.collectAsState()
    val onAngleChange: (Float) -> Unit = { viewModel.angleState.value = it.toDouble() }
    val focusManager = LocalFocusManager.current

    Text(
        text = stringResource(id = R.string.slope_label),
        style = MaterialTheme.typography.titleLarge
    )

    Slider(
        value = angle.toFloat(),
        onValueChange = {
            onAngleChange(it)
            focusManager.clearFocus()
        },
        valueRange = 0f..90f,
        modifier = Modifier.fillMaxWidth()
    )

    InfoHelpButton("${stringResource(id = R.string.angle)} ${angle.toInt()}°", stringResource(id = R.string.roofAngleHelp))
}

@Composable
fun DirectionSlider(
    viewModel: MapViewModel
) {
    val azimuth by viewModel.directionState.stateFlow.collectAsState()
    val onAzimuthChange: (Float) -> Unit = { viewModel.directionState.value = it.toDouble() }
    val focusManager = LocalFocusManager.current

    Text(
        text = stringResource(id = R.string.direction_label),
        style = MaterialTheme.typography.titleLarge
    )

    Slider(
        value = azimuth.toFloat(),
        onValueChange = {
            onAzimuthChange(it)
            focusManager.clearFocus()
        },
        valueRange = 0f..315f,
        modifier = Modifier.fillMaxWidth()
    )

    InfoHelpButton("${stringResource(id = R.string.direction)} ${azimuth.toInt()}° (${getCompassDirection(azimuth.toInt())})", stringResource(id = R.string.panelDirectionHelp))
}

@Composable
fun PanelPicker(viewModel: MapViewModel, focusManager: FocusManager) {
    val panelTypes = remember {
        listOf(
            SolarPanelType("Monokrystallinsk", 20f, "Høy effektivitet, dyrere, Standard"),
            SolarPanelType("Polykrystallinsk", 15f, "Middels effektivitet, rimeligere"),
            SolarPanelType("Tynnfilm", 10f, "Lav effektivitet, fleksibel")
        )
    }

    val efficiency by viewModel.efficiencyState.stateFlow.collectAsState()

    Text(stringResource(id = R.string.efficiency_label), style = MaterialTheme.typography.titleLarge)

    panelTypes.forEach { panelType ->
        PanelTypeCard(panelType, efficiency.toFloat(), viewModel, focusManager)
    }

    LearnMoreLink()
    InfoHelpButton("${stringResource(id = R.string.efficiency)} ~ ${efficiency.toInt()}%", stringResource(id = R.string.panelEfficencyHelp))

}

@Composable
fun PanelTypeCard(
    panelType: SolarPanelType,
    selectedEfficiency: Float,
    viewModel: MapViewModel,
    focusManager: FocusManager
) {
    val onSelected: (Float) -> Unit = { viewModel.efficiencyState.value = it.toDouble() }
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
                viewModel.efficiencyState.value = panelType.efficiency.toDouble()
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
    viewModel: MapViewModel,
    navController: NavController,
) {
    //if (viewModel.area > 0 && viewModel.coordinates != null) {
        Button(
            onClick = {
                navController.navigate("result")
            },
            //modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Gå til resultater")
        }
    //}
}
