package no.solcellepanelerApp.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
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
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import no.solcellepanelerApp.MainActivity
import no.solcellepanelerApp.R
import no.solcellepanelerApp.model.electricity.Region
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.font.FontSizeState
import no.solcellepanelerApp.ui.navigation.AdditionalInputBottomSheet
import no.solcellepanelerApp.ui.navigation.AppearanceBottomSheet
import no.solcellepanelerApp.ui.navigation.BottomBar
import no.solcellepanelerApp.ui.navigation.HelpBottomSheet
import no.solcellepanelerApp.ui.navigation.TopBar
import no.solcellepanelerApp.ui.result.WeatherViewModel
import no.solcellepanelerApp.ui.reusables.SimpleTutorialOverlay
import no.solcellepanelerApp.ui.theme.darkGrey
import no.solcellepanelerApp.ui.theme.lightBlue
import no.solcellepanelerApp.ui.theme.lightGrey
import no.solcellepanelerApp.ui.theme.orange
import no.solcellepanelerApp.util.RequestLocationPermission
import no.solcellepanelerApp.util.fetchCoordinates


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
    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    var showMapOverlay by remember { mutableStateOf(true) }
    var showDrawOverlay by remember { mutableStateOf(false) }

    val message = stringResource(R.string.address_not_found)

    LaunchedEffect(trigger) {
        if (trigger > lastShownTrigger) {
            snackbarHostState.showSnackbar(message)
            lastShownTrigger = trigger
        }
    }
    if (showMapOverlay) {
        SimpleTutorialOverlay(
            onDismiss = { showMapOverlay = false },
            message = stringResource(R.string.map_overlay)
        )
    }

    if (showDrawOverlay) {
        SimpleTutorialOverlay(
            onDismiss = { showDrawOverlay = false },
            message = stringResource(R.string.map_draw_overlay)
        )
    }
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
            DisplayScreen(
                viewModel = viewModel,
                navController = navController,
                weatherViewModel = weatherViewModel,
                setShowDrawOverlay = { showDrawOverlay = it }
            )
        }
    }
    HelpBottomSheet(
        navController = navController,
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
    setShowDrawOverlay: (Boolean) -> Unit,


    ) {
    val context = LocalContext.current
    var selectedCoordinates by remember { mutableStateOf<LatLng?>(null) }
    val markerState = rememberMarkerState(position = selectedCoordinates ?: LatLng(0.0, 0.0))
    val coroutineScope = rememberCoroutineScope()
    var address by remember { mutableStateOf("") }
    val coordinates by viewModel.coordinates.observeAsState()
    val height by viewModel.height.collectAsState()
    val polygonPoints = viewModel.polygondata
    var isPolygonvisible by remember { mutableStateOf(false) }
    var drawingEnabled by remember { mutableStateOf(false) }
    var index by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showMissingLocationDialog by remember { mutableStateOf(false) }


    val activity = (context as? MainActivity)
    var selectedRegion by rememberSaveable { mutableStateOf<Region?>(null) }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var locationPermissionGranted by remember { mutableStateOf(false) }

    RequestLocationPermission { region ->
        selectedRegion = region
        locationPermissionGranted = true

    }

    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted && activity != null) {
            val location = fetchCoordinates(activity)
            currentLocation = location
        }
    }

    //Camera and map state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(59.9436145, 10.7182883), 18f)
    }

    val mapUiSettings = remember {
        MapUiSettings()
    }



    LaunchedEffect(Unit) {
        locationPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
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
            uiSettings = mapUiSettings.copy(
//                scrollGesturesEnabled = !drawingEnabled,
//                zoomGesturesEnabled = !drawingEnabled
            ),
            onMapClick = { latLng ->
                if (drawingEnabled) {
                    viewModel.addPoint(latLng)
                    //index++
                } else {
                    selectedCoordinates = latLng
                    viewModel.selectLocation(latLng.latitude, latLng.longitude)
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(
                                latLng,
                                cameraPositionState.position.zoom
                            )
                        )
                    }
                }
            }
        ) {
            //Map markers and polygons
            if (!drawingEnabled) {
                selectedCoordinates?.let {
                    markerState.position = it
                    MapMarker(
                        state = markerState,
                        title = stringResource(id = R.string.selected_position),
                        snippet = "Lat: ${it.latitude}, Lng: ${it.longitude}",
                        draggable = false,
                        context = LocalContext.current
                    )
                }
            } else {
                polygonPoints.forEachIndexed { index, point ->
                    val pointMarkerState = rememberMarkerState(position = point)

                    LaunchedEffect(pointMarkerState) {
                        snapshotFlow { pointMarkerState.position }
                            .collect { newPosition ->
                                viewModel.updatePoint(index, newPosition)
                            }
                    }

                    MapMarker(
                        state = pointMarkerState,
                        title = "${stringResource(id = R.string.point)} ${index + 1}",
                        context = LocalContext.current,
                        draggable = true
                    )
                }

                if (isPolygonvisible) {
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
                selectedCoordinates = newLatLng
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(newLatLng, 18f)
                    )
                )
            }
        }


        //Search bar

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            colors = CardColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                disabledContentColor = MaterialTheme.colorScheme.background,
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AddressInputField(
                    value = address,
                    onValueChange = {
                        address = it
                        viewModel.addressFetchError.value = false
                    },
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
                    ),
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RectangleShape,
                    onClick = {
                        viewModel.fetchCoordinates(address)
                    },
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = MaterialTheme.colorScheme.primary,
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


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .zIndex(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val rememberedScope = rememberCoroutineScope()

            Spacer(
                modifier = Modifier.height(80.dp)
            )

            if (selectedCoordinates != null) {
                Button(
                    content = {
                        Text(stringResource(id = R.string.confirm_location))
                    },
                    onClick = {
                        if (coordinates != null) {
                            showBottomSheet = true
                            selectedCoordinates = null
                            viewModel.removePoints()
                            index = 0
                            rememberedScope.launch {
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
            }

            if (isPolygonvisible) {
                val calculatedArea = viewModel.calculateAreaOfPolygon(polygonPoints).toString()
                Text(
                    text = stringResource(R.string.area_drawn) + " $calculatedArea m²",
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
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                    ) {

                        Button(
                            enabled = locationPermissionGranted,
                            onClick = {
                                mapUseLocation(
                                    currentLocation,
                                    locationPermissionGranted,
                                    cameraPositionState,
                                    rememberedScope
                                )
                            },
                            colors = ButtonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = darkGrey,
                                disabledContentColor = lightGrey,
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = if (locationPermissionGranted) R.drawable.baseline_my_location_24 else R.drawable.baseline_location_disabled_24),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = if (locationPermissionGranted) MaterialTheme.colorScheme.primary else lightGrey
                            )
                        }
                    }
                }
            }
            if (drawingEnabled) {
                DrawingControls(
                    polygonPoints = polygonPoints,
                    viewModel = viewModel,
                    toggleBottomSheet = { showBottomSheet = true },
                    onToggleVisibility = { isPolygonvisible = !isPolygonvisible }
                )
            }

            if (showMissingLocationDialog) {
                LocationNotSelectedDialog(
                    onDismiss = { showMissingLocationDialog = false },
                    coordinates = coordinates
                )
            }
        }
        AdditionalInputBottomSheet(
            visible = showBottomSheet,
            onDismiss = { showBottomSheet = false },
            onStartDrawing = {
                drawingEnabled = true
                setShowDrawOverlay(true)
                selectedCoordinates = null
                viewModel.removePoints()
                index = 0
            },
            coordinates = coordinates,
            height = height,
            area = viewModel.areaInput,
            navController = navController,
            viewModel = viewModel,
            weatherViewModel = weatherViewModel,
            selectedRegion = selectedRegion,
            onRegionSelected = { selectedRegion = it },
        )
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
        textStyle = TextStyle(fontFamily = FontFamily.Default),
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


