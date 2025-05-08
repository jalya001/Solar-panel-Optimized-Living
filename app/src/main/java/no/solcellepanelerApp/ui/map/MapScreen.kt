package no.solcellepanelerApp.ui.map

import android.content.Context
import android.graphics.Canvas
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import no.solcellepanelerApp.MainActivity
import no.solcellepanelerApp.R
import no.solcellepanelerApp.ui.font.FontSizeState
import no.solcellepanelerApp.ui.result.WeatherViewModel
import no.solcellepanelerApp.ui.reusables.AppScaffoldController
import no.solcellepanelerApp.ui.reusables.SimpleTutorialOverlay
import no.solcellepanelerApp.ui.theme.darkGrey
import no.solcellepanelerApp.ui.theme.lightBlue
import no.solcellepanelerApp.ui.theme.lightGrey
import no.solcellepanelerApp.ui.theme.orange
import no.solcellepanelerApp.util.RequestLocationPermission

@Composable
fun MapScreen(
    viewModel: MapScreenViewModel,
    navController: NavController,
    weatherViewModel: WeatherViewModel,
    appScaffoldController: AppScaffoldController,
    contentPadding: PaddingValues
) {
    var showMapOverlay by remember { mutableStateOf(true) }
    var showDrawOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessages.collect { message ->
            appScaffoldController.showSnackbar(message)
        }
    }

    if (showMapOverlay) {
        SimpleTutorialOverlay(
            onDismiss = { showMapOverlay = false },
            message = "Søk etter adressen din, bruk enhetsposisjon, eller trykk på kartet for å velge lokasjon"
        )
    }

    if (showDrawOverlay) {
        SimpleTutorialOverlay(
            onDismiss = { showDrawOverlay = false },
            message = "Trykk på kartet for å starte tegningen av ønsket område"
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DisplayScreen(
            viewModel = viewModel,
            navController = navController,
            weatherViewModel = weatherViewModel,
        )
    }
}

@Composable
fun DisplayScreen(
    viewModel: MapScreenViewModel,
    navController: NavController,
    weatherViewModel: WeatherViewModel,
) {
    val context = LocalContext.current
    val activity = context as? MainActivity

    val coordinates by viewModel.coordinates.observeAsState()
    val height by viewModel.height.collectAsState()
    val selectedCoordinates = viewModel.selectedCoordinates
    val polygonPoints = viewModel.polygonData
    val isPolygonVisible = viewModel.isPolygonVisible
    val drawingEnabled by viewModel.drawingEnabled.collectAsState()
    Log.d("drawing from screen is :",drawingEnabled.toString())
    val showBottomSheet = viewModel.showBottomSheet
    val showMissingLocationDialog = viewModel.showMissingLocationDialog
    val locationPermissionGranted = viewModel.locationPermissionGranted
    val markerState = rememberMarkerState(position = selectedCoordinates ?: LatLng(0.0, 0.0))

    val cameraPositionState = viewModel.cameraPositionState
    val mapUiSettings = viewModel.mapUiSettings

    val coroutineScope = rememberCoroutineScope()

    RequestLocationPermission { region ->
        viewModel.selectedRegion = region
        viewModel.locationPermissionGranted = true
    }

    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted && activity != null) {
            viewModel.fetchCurrentLocation(activity)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkLocationPermission(context)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = MapType.HYBRID,
                isMyLocationEnabled = locationPermissionGranted
            ),
            uiSettings = mapUiSettings,
            onMapClick = { latLng ->
                if (drawingEnabled) {
                    viewModel.addPoint(latLng)
                } else {
                    viewModel.selectLocation(latLng.latitude, latLng.longitude)
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(latLng, cameraPositionState.position.zoom)
                        )
                    }
                }
            }
        ) {
            if (!drawingEnabled) {
                selectedCoordinates?.let {
                    markerState.position = it
                    MapMarker(
                        state = markerState,
                        title = stringResource(id = R.string.selected_position),
                        snippet = "Lat: ${it.latitude}, Lng: ${it.longitude}",
                        draggable = false,
                        context = context
                    )
                }
            } else {
                polygonPoints.forEachIndexed { index, point ->
                    val pointMarkerState = rememberMarkerState(position = point)

                    LaunchedEffect(pointMarkerState) {
                        snapshotFlow { pointMarkerState.position }
                            .collect { newPosition -> viewModel.updatePoint(index, newPosition) }
                    }

                    MapMarker(
                        state = pointMarkerState,
                        title = "${stringResource(id = R.string.point)} ${index + 1}",
                        context = context,
                        draggable = true
                    )
                }

                if (isPolygonVisible) {
                    Polygon(
                        points = polygonPoints.toList(),
                        fillColor = lightBlue.copy(0.6f),
                        strokeColor = lightBlue,
                        strokeWidth = 5f
                    )
                }
            }
        }

        LaunchedEffect(coordinates) {
            coordinates?.let { (lat, lon) ->
                val newLatLng = LatLng(lat, lon)
                viewModel.selectLocation(lat, lon)
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(newLatLng, 18f)
                    )
                )
            }
        }

        SearchBar(
            address = viewModel.address,
            onAddressChange = { viewModel.address = it },
            onSearch = { viewModel.fetchCoordinates(viewModel.address) },
            viewModel = viewModel
        )

        ControlsColumn(
            selectedCoordinates = selectedCoordinates,
            coordinates = coordinates,
            polygonPoints = polygonPoints,
            isPolygonVisible = isPolygonVisible,
            onConfirmLocation = {
                if (coordinates != null) {
                    viewModel.showBottomSheet = true
                    viewModel.clearSelection()
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
                    viewModel.showMissingLocationDialog = true
                }
            },
            areaText = viewModel.calculateAreaOfPolygon(polygonPoints).toString(),
            onMapUseLocationClick = {
                mapUseLocation(
                    viewModel.currentLocation,
                    locationPermissionGranted,
                    cameraPositionState,
                    coroutineScope
                )
            },
            locationPermissionGranted = locationPermissionGranted,
            drawingEnabled = drawingEnabled,
            showMissingLocationDialog = showMissingLocationDialog,
            onDismissDialog = { viewModel.showMissingLocationDialog = false },
            onTogglePolygonVisibility = { viewModel.togglePolygonVisibility() },
            viewModel = viewModel,
            onToggleBottomSheet = { viewModel.showBottomSheet = true }
        )

        AdditionalInputBottomSheet(
            visible = showBottomSheet,
            onDismiss = { viewModel.showBottomSheet = false },
            coordinates = coordinates,
            height = height,
            area = viewModel.areaInput,
            navController = navController,
            viewModel = viewModel,
            weatherViewModel = weatherViewModel,
            selectedRegion = viewModel.selectedRegion,
            onRegionSelected = { viewModel.selectedRegion = it },
        )
    }
}

@Composable
fun ControlsColumn(
    selectedCoordinates: LatLng?,
    coordinates: Pair<Double, Double>?,
    polygonPoints: List<LatLng>,
    isPolygonVisible: Boolean,
    onConfirmLocation: () -> Unit,
    areaText: String,
    onMapUseLocationClick: () -> Unit,
    locationPermissionGranted: Boolean,
    drawingEnabled: Boolean,
    showMissingLocationDialog: Boolean,
    onDismissDialog: () -> Unit,
    onTogglePolygonVisibility: () -> Unit,
    viewModel: MapScreenViewModel,
    onToggleBottomSheet: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .zIndex(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        if (selectedCoordinates != null) {
            Button(
                onClick = onConfirmLocation
            ) {
                Text(stringResource(id = R.string.confirm_location))

            }
        }

        if (isPolygonVisible) {
            Text(
                text = stringResource(R.string.area_drawn) + " $areaText m²",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .zIndex(1f)
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(8.dp)
            )
        }

        if (!drawingEnabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Button(
                        enabled = locationPermissionGranted,
                        onClick = onMapUseLocationClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = darkGrey,
                            disabledContentColor = lightGrey,
                        )
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (locationPermissionGranted)
                                    R.drawable.baseline_my_location_24
                                else
                                    R.drawable.baseline_location_disabled_24
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp),
                            tint = if (locationPermissionGranted)
                                MaterialTheme.colorScheme.primary
                            else lightGrey
                        )
                    }
                }
            }
        }

        if (drawingEnabled) {
            DrawingControls(
                polygonPoints = polygonPoints,
                viewModel = viewModel,
                toggleBottomSheet = onToggleBottomSheet,
                onToggleVisibility = onTogglePolygonVisibility
            )
        }

        if (showMissingLocationDialog) {
            LocationNotSelectedDialog(
                onDismiss = onDismissDialog,
                coordinates = coordinates
            )
        }
    }
}

@Composable
fun SearchBar(
    address: String,
    onAddressChange: (String) -> Unit,
    onSearch: () -> Unit,
    viewModel: MapScreenViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AddressInputField(
                value = address,
                onValueChange = onAddressChange,
                address = address,
                viewModel = viewModel,
                label = stringResource(id = R.string.enter_address),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    cursorColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedTextColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainer,
                )
            )

            Button(
                modifier = Modifier.height(80.dp),
                shape = RectangleShape,
                onClick = onSearch,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(id = R.string.search_coordinates),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}



@Composable
fun AddressInputField(
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
        ))
}

@Composable
fun LocationNotSelectedDialog(
    coordinates: Pair<Double, Double>?,
    onDismiss: () -> Unit,
) {
    val showDialog by remember { mutableStateOf(coordinates == null) }

    if (showDialog && coordinates == null) {
        val currentDensity = LocalDensity.current
        val customDensity = Density(
            density = currentDensity.density,
            fontScale = FontSizeState.fontScale.floatValue
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

@Composable
private fun DrawingControls(
    polygonPoints: List<LatLng>,
    viewModel: MapScreenViewModel,
    onToggleVisibility: () -> Unit,
    toggleBottomSheet: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
//            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
//                .width(300.dp)
        ) {
            var areaShown by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .width(270.dp)
                    .height(200.dp)
            )
            {
                if (areaShown) {
                    Card(
                        modifier = Modifier
                            .clickable {
                                viewModel.areaInput =
                                    viewModel.calculateAreaOfPolygon(polygonPoints).toString()
                                viewModel.drawingEnabled= MutableStateFlow(false)
                                viewModel.togglePolygonVisibility()
                                viewModel.removePoints()
                                toggleBottomSheet()
                            }
                            .width(130.dp)
                            .align(Alignment.TopEnd),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.background,
                            disabledContainerColor = Color(0xFF4CAF50),
                            disabledContentColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = "Confirm Drawing",
                                modifier = Modifier.padding(top = 6.dp),
                            )
                            Text(
                                text = stringResource(id = R.string.confirm_drawing),
                                modifier = Modifier
                                    .padding(
                                        top = 2.dp,
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 10.dp
                                    )
                                    .width(130.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.background
                            )
                        }
                    }
                }


                if (polygonPoints.size >= 3) {
                    Card(
                        modifier = Modifier
                            .clickable {
                                onToggleVisibility()
                                areaShown = !areaShown
                            }
                            .width(130.dp)
                            .align(Alignment.TopStart),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.background,
                            disabledContainerColor = Color(0xFF4CAF50),
                            disabledContentColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (!areaShown) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Show area",
                                modifier = Modifier.padding(top = 6.dp),
                            )
                            Text(
                                text = stringResource(id = if (!areaShown) R.string.show_area else R.string.hide_area),
                                modifier = Modifier
                                    .padding(
                                        top = 2.dp,
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 10.dp
                                    )
                                    .width(130.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.background
                            )
                        }
                    }

                }

                if (polygonPoints.size >= 2) {
                    Card(
                        modifier = Modifier
                            .clickable {
                                viewModel.removePoints()
                                onToggleVisibility()
                            }
                            .width(130.dp)
                            .align(Alignment.BottomEnd),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            disabledContainerColor = Color(0xFF4CAF50),
                            disabledContentColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.DeleteForever,
                                contentDescription = "remove all points",
                                modifier = Modifier.padding(top = 6.dp),
                            )
                            Text(
                                text = stringResource(id = R.string.remove_points),
                                modifier = Modifier
                                    .padding(
                                        top = 2.dp,
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 10.dp
                                    )
                                    .width(130.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                    }
                }

                if (polygonPoints.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .clickable {
                                viewModel.removeLastPoint()
//                                onToggleVisibility
                            }
                            .width(130.dp)
                            .align(Alignment.BottomStart),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            disabledContainerColor = Color(0xFF4CAF50),
                            disabledContentColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "remove last point",
                                modifier = Modifier.padding(top = 6.dp),
                            )
                            Text(
                                text = stringResource(id = R.string.remove_last_point),
                                modifier = Modifier
                                    .padding(
                                        top = 2.dp,
                                        start = 10.dp,
                                        end = 10.dp,
                                        bottom = 10.dp
                                    )
                                    .width(130.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }


        }
    }


}

// hentet fra https://stackoverflow.com/questions/70598043/how-to-use-custom-icon-of-google-maps-marker-in-compose

@Composable
fun MapMarker(
    context: Context,
    title: String,
    snippet: String? = null,
    state: MarkerState,
    draggable: Boolean = true,
) {

    val iconResourceId =
        R.drawable.baseline_location_pin_24

    val tintColor = orange.toArgb()

    val icon =
        bitmapDescriptor(context, iconResourceId, width = 120, height = 120, tint = tintColor)

    if (snippet != null) {
        Marker(
            state = state,
            title = title,
            snippet = snippet,
            icon = icon,
            draggable = draggable
        )
    } else {
        Marker(
            state = state,
            title = title,
            icon = icon,
            draggable = draggable
        )
    }
}

fun bitmapDescriptor(
    context: Context,
    vectorResId: Int,
    width: Int = 100,
    height: Int = 100,
    tint: Int? = null,
): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null

    if (tint != null) {
        drawable.setTint(tint)
    }

    val originalBitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)

    val canvas = Canvas(originalBitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    val scaledBitmap = originalBitmap.scale(width, height, false)
    return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
}

fun mapUseLocation(
    currentLocation: Location?,
    locationPermissionGranted: Boolean,
    cameraPositionState: CameraPositionState,
    coroutineScope: CoroutineScope,
) {
    if (currentLocation != null && locationPermissionGranted) {
        val newPosition = CameraPosition.fromLatLngZoom(
            LatLng(currentLocation.latitude, currentLocation.longitude),
            18f
        )
        coroutineScope.launch {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(newPosition),
                durationMs = 1000
            )
        }
        Log.d("Lat", "${currentLocation.latitude}")
        Log.d("Long", "${currentLocation.longitude}")
    }
}

