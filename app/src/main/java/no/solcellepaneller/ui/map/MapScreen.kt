package no.solcellepaneller.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import no.solcellepaneller.R
import no.solcellepaneller.ui.font.FontScaleViewModel
import no.solcellepaneller.ui.font.FontSizeState
import no.solcellepaneller.ui.navigation.AdditionalInputBottomSheet
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.HelpBottomSheet
import no.solcellepaneller.ui.navigation.TopBar
import no.solcellepaneller.ui.result.WeatherViewModel

@Composable
fun MapScreen(
    viewModel: MapScreenViewModel,
    navController: NavController,
    fontScaleViewModel: FontScaleViewModel,
    weatherViewModel: WeatherViewModel,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val trigger by viewModel.snackbarMessageTrigger

    var lastShownTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(trigger) {
        if (trigger > lastShownTrigger) {
            snackbarHostState.showSnackbar("Address not found, try again")
            lastShownTrigger = trigger
        }
    }
    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopBar(
                navController = navController,
                text = stringResource(id = R.string.map_title)
            )
        },
        bottomBar = {
            BottomBar(
                onHelpClicked = { showHelp = true },
                onAppearanceClicked = { showAppearance = true },
                navController = navController
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DisplayScreen(viewModel, navController, weatherViewModel)
        }
    }
    HelpBottomSheet(
        visible = showHelp,
        onDismiss = { showHelp = false },
    )
    AppearanceBottomSheet(
        visible = showAppearance,
        onDismiss = { showAppearance = false },
        fontScaleViewModel = fontScaleViewModel
    )
}

@Composable
fun DisplayScreen(
    viewModel: MapScreenViewModel,
    navController: NavController,
    weatherViewModel: WeatherViewModel,
) {
    var address by remember { mutableStateOf("") }
    val coordinates by viewModel.coordinates.observeAsState()
    var selectedCoordinates by remember { mutableStateOf<LatLng?>(null) }
    val polygonPoints = viewModel.polygondata
    var ispolygonvisible by remember { mutableStateOf(false) }
    var drawingEnabled by remember { mutableStateOf(false) }
    var index by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var arealatlong by remember { mutableStateOf<LatLng?>(null) }
    var showMissingLocationDialog by remember { mutableStateOf(false) }


    var showHelpSheet by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(59.9436145, 10.7182883), 18f)
    }

    val markerState = rememberMarkerState(position = selectedCoordinates ?: LatLng(0.0, 0.0))
    val coroutineScope = rememberCoroutineScope()
    val mapUiSettings = remember {
        MapUiSettings()
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = MapType.HYBRID),
            uiSettings = mapUiSettings.copy(
                scrollGesturesEnabled = !drawingEnabled,
                zoomGesturesEnabled = !drawingEnabled
            ),
            onMapClick = { latLng ->
                if (drawingEnabled) {
                    viewModel.addPoint(latLng)
                    index++
                } else {
                    selectedCoordinates = latLng
                    viewModel.selectLocation(latLng.latitude, latLng.longitude)
                }
            }
        ) {
            if (!drawingEnabled) {
                selectedCoordinates?.let {
                    markerState.position = it
                    Marker(
                        state = markerState,
                        title = stringResource(id = R.string.selected_position),
                        snippet = "Lat: ${it.latitude}, Lng: ${it.longitude}"
                    )
                }
            } else {
                polygonPoints.forEachIndexed { i, point ->
                    Marker(
                        state = rememberMarkerState(position = point),
                        title = stringResource(id = R.string.point) + " ${i + 1}"
                    )
                }
                if (ispolygonvisible) {
                    Polygon(
                        points = polygonPoints,
                        fillColor = Green.copy(0.3f),
                        strokeColor = Green,
                        strokeWidth = 5f
                    )
                }
            }
        }

        LaunchedEffect(coordinates) {
            coordinates?.let { latLng ->
                val newLatLng = LatLng(latLng.first, latLng.second)
                if (selectedCoordinates != newLatLng) {
                    selectedCoordinates = newLatLng
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition(
                                    newLatLng,
                                    cameraPositionState.position.zoom,
                                    0f,
                                    cameraPositionState.position.bearing
                                )
                            )
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InputField(
                    value = address,
                    onValueChange = {
                        address = it
                        viewModel.addressFetchError.value = false
                    },
                    address = address,
                    viewModel = viewModel,
                    label = stringResource(id = R.string.enter_address),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                        focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                        cursorColor = MaterialTheme.colorScheme.tertiary,
                        unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    ),
//                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
//                    keyboardActions = KeyboardActions(
//                        onDone = {
//                            keyboardController?.hide()
//                            viewModel.fetchCoordinates(address)
//                        }
//                    )
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RectangleShape,
                    onClick = {
                        viewModel.fetchCoordinates(address)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(id = R.string.search_coordinates),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .zIndex(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            val coroutineScope = rememberCoroutineScope()

            BekreftLokasjon(
                onClick = {
                    if (coordinates != null) {
                        showBottomSheet = true
                        selectedCoordinates = null
                        viewModel.removePoints()
                        index = 0
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition(
                                        cameraPositionState.position.target,
                                        19f,
                                        0f,
                                        cameraPositionState.position.bearing
                                    )
                                )
                            )
                        }
                    } else {
                        showMissingLocationDialog = true
                    }
                }
            )

            if (showMissingLocationDialog) {
                LocationNotSelectedDialog(
                    coordinates = coordinates,
                    onDismiss = { showMissingLocationDialog = false },
                )
            }

            var area by remember { mutableStateOf("") }

            AdditionalInputBottomSheet(
                visible = showBottomSheet,
                onDismiss = { showBottomSheet = false },
                onStartDrawing = {
                    drawingEnabled = true
                    selectedCoordinates = null
                    viewModel.removePoints()
                    index = 0
                },
                coordinates = coordinates,
                area = area,
                navController = navController,
                viewModel = viewModel,
                weatherViewModel = weatherViewModel
            )

            if (drawingEnabled) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(alignment = Alignment.Start)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .align(alignment = Alignment.BottomStart)
                    ) {
                        var areaShown by remember { mutableStateOf(false) }

                        Button(
                            onClick = {
                                area = ""
                                drawingEnabled = false
                                ispolygonvisible = false
                                viewModel.removePoints()
                                index = 0
                            },
                            colors = ButtonDefaults.buttonColors(Color.Gray)
                        ) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                        if (polygonPoints.size > 3) {
                            Button(onClick = {
                                ispolygonvisible = !ispolygonvisible
                                arealatlong = cameraPositionState.position.target
                                val calculatedArea = viewModel.calculateAreaOfPolygon(polygonPoints)
                                area = calculatedArea.toString()
                                areaShown = true
                            }, colors = ButtonDefaults.buttonColors(Color(color = 0xFF4CAF50))) {
                                Text(text = stringResource(id = R.string.show_area))
                            }

                            if (areaShown && ispolygonvisible) {
                                Button(onClick = {
                                    showBottomSheet = true
                                }) {
                                    Text(text = stringResource(id = R.string.confirm_drawing))
                                }
                            }
                        }

                        if (polygonPoints.isNotEmpty()) {
                            Button(onClick = {
                                viewModel.removeLastPoint()
                                ispolygonvisible = false
                                index -= 1
                            }, colors = ButtonDefaults.buttonColors(Color.Yellow)) {
                                Text(text = stringResource(id = R.string.remove_last_point))
                            }
                            Button(onClick = {
                                viewModel.removePoints()
                                ispolygonvisible = false
                                index = 0
                            }, colors = ButtonDefaults.buttonColors(Red)) {
                                Text(text = stringResource(id = R.string.remove_points))
                            }
                        }
                    }

                }
            }
        }
    }

}

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    colors: TextFieldColors,
    viewModel: MapScreenViewModel,
    address: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        colors = colors,
        singleLine = true,
        modifier = Modifier
            .width(300.dp)
            .padding(bottom = 10.dp, start = 10.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                viewModel.fetchCoordinates(address)
            }
        ),
        //burde egt ikke være hardkodet
    )
}

@Composable
fun BekreftLokasjon(
    //må huske å endre navn på funksjoner ogsånt til engelsk
    onClick: () -> Unit,
) {
    Button(onClick = onClick) {
        Text(stringResource(id = R.string.confirm_location))
    }
}

@Composable
fun LocationNotSelectedDialog(
    coordinates: Pair<Double, Double>?,
    onDismiss: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(coordinates == null) }
    var showHelpBottomSheet by remember { mutableStateOf(false) }
    if (showHelpBottomSheet) {
        HelpBottomSheet(
            visible = true,
            onDismiss = { showHelpBottomSheet = false },
            expandSection = "draw",
        )
    }
    if (showDialog && coordinates == null) {
        val currentDensity = LocalDensity.current
        val customDensity = Density(
            density = currentDensity.density,
            fontScale = FontSizeState.fontScale.value
        )

        CompositionLocalProvider(LocalDensity provides customDensity) {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                    Text(stringResource(id = R.string.no_location_title))
                },
                text = {
                    Text(stringResource(id = R.string.no_location_message))
                },
                confirmButton = {
//                Button( //Ga ikke mening å ha det på denne skjermen
//                    onClick ={
//                        showHelpBottomSheet = true
//                    }
//                ) {
//                  Text(stringResource(id = R.string.need_help_drawing))
//                }
                },
                dismissButton = {
                    Button(
                        onClick = onDismiss
                    ) {
                        Text(stringResource(id = R.string.dismiss))
                    }
                }


            )
        }

    }
}

