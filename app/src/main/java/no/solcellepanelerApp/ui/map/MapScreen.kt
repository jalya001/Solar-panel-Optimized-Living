package no.solcellepanelerApp.ui.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
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
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import no.solcellepanelerApp.R
import no.solcellepanelerApp.data.location.LocationService
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.font.FontSizeState
import no.solcellepanelerApp.ui.navigation.AdditionalInputBottomSheet
import no.solcellepanelerApp.ui.navigation.AppearanceBottomSheet
import no.solcellepanelerApp.ui.navigation.BottomBar
import no.solcellepanelerApp.ui.navigation.HelpBottomSheet
import no.solcellepanelerApp.ui.navigation.TopBar
import no.solcellepanelerApp.ui.result.WeatherViewModel
import java.util.Locale


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

    LaunchedEffect(trigger) {
        if (trigger > lastShownTrigger) {
            snackbarHostState.showSnackbar("Address not found, try again")
            lastShownTrigger = trigger
        }
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
                snackbarHostState = snackbarHostState,
                modifier = Modifier.padding(contentPadding)
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayScreen(
    viewModel: MapScreenViewModel,
    navController: NavController,
    weatherViewModel: WeatherViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var selectedCoordinates by remember { mutableStateOf<LatLng?>(null) }
    val markerState = rememberMarkerState(position = selectedCoordinates ?: LatLng(0.0, 0.0))
    val coroutineScope = rememberCoroutineScope()
    var address by remember { mutableStateOf("") }
    val coordinates by viewModel.coordinates.observeAsState()
    val polygonPoints = viewModel.polygondata
    var isPolygonvisible by remember { mutableStateOf(false) }
    var drawingEnabled by remember { mutableStateOf(false) }
    var index by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var arealatlong by remember { mutableStateOf<LatLng?>(null) }
    var showMissingLocationDialog by remember { mutableStateOf(false) }

    //Location permission state
    var locationPermissionGranted by remember { mutableStateOf(false) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        locationPermissionGranted = isGranted
        if (!isGranted) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Location permission required")
            }
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
                scrollGesturesEnabled = !drawingEnabled,
                zoomGesturesEnabled = !drawingEnabled
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
//                    Marker(
//                        state = markerState,
//                        title = stringResource(id = R.string.selected_position),
//                        snippet = "Lat: ${it.latitude}, Lng: ${it.longitude}",
//                    )
                    MapMarker(
                        state = markerState,
                        title = stringResource(id = R.string.selected_position),
                        snippet = "Lat: ${it.latitude}, Lng: ${it.longitude}",
                        context = LocalContext.current,
                    )
                }
            } else {
                polygonPoints.forEachIndexed { i, point ->
//                    Marker(
//                        state = rememberMarkerState(position = point),
//                        title = "${stringResource(id = R.string.point)} ${i + 1}"
//                    )
                    MapMarker(
                        state = rememberMarkerState(position = point),
                        title = "${stringResource(id = R.string.point)} ${i + 1}",
                        context = LocalContext.current,
                    )
                }
                if (isPolygonvisible) {
                    Polygon(
                        points = polygonPoints,
                        fillColor = MaterialTheme.colorScheme.primary.copy(0.6f),
                        strokeColor = MaterialTheme.colorScheme.primary,
//                        fillColor = Green.copy(0.3f),
//                        strokeColor = Green,
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

//        funker ikke rn
//        var detectedAddress by remember { mutableStateOf("") }
//        LocationButton(
//            locationPermissionGranted = locationPermissionGranted,
//            onAddressDetected = { address ->
//                detectedAddress = address
//            }
//        )

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


                AdressInputField(
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

            Spacer(modifier = Modifier.height(60.dp))

            val coroutineScope = rememberCoroutineScope()

            Spacer(
                modifier = Modifier.height(20.dp)
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
            }

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
                DrawingControls(
                    polygonPoints = polygonPoints,
                    viewModel = viewModel,
                    toggleBottomSheet = { showBottomSheet = true },
                    onCancel = {
                        drawingEnabled = false
                        isPolygonvisible = false
                        viewModel.removePoints()
                    },
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
                selectedCoordinates = null
                viewModel.removePoints()
            },
            coordinates = coordinates,
            area = viewModel.calculateAreaOfPolygon(polygonPoints).toString(),
            navController = navController,
            viewModel = viewModel,
            weatherViewModel = weatherViewModel
        )
    }
}

fun getAddressFromLocation(context: Context, location: Location): String? {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        addresses?.firstOrNull()?.getAddressLine(0)
    } catch (e: Exception) {
        Log.e("Geocoder", "Feil ved henting av adresse", e)
        null
    }
}


@Composable
fun AdressInputField(
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
//    var showHelpBottomSheet by remember { mutableStateOf(false) }
//    if (showHelpBottomSheet) {
//        HelpBottomSheet(
//            visible = true,
//            onDismiss = { showHelpBottomSheet = false },
//            expandSection = "draw",
//        )
//    }

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


//                Button( //Ga ikke mening å ha det på denne skjermen, men burde være på tegneskjermen
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.no_location_title)) },
        text = { Text(stringResource(id = R.string.no_location_message)) },
        confirmButton = {},
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.dismiss))
            }
        }
    )
}

@Composable
private fun DrawingControls(
    polygonPoints: List<LatLng>,
    viewModel: MapScreenViewModel,
    onCancel: () -> Unit,
    onToggleVisibility: () -> Unit,
    toggleBottomSheet: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
        ) {

            Column {
                var areaShown by remember { mutableStateOf(false) }
                if (areaShown) {
                    Button(
                        onClick = {
                            toggleBottomSheet()
                        },
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            disabledContainerColor = Color(color = 0xFF4CAF50),
                            disabledContentColor = Color(color = 0xFF4CAF50)
                        )
                    ) {
                        Text(text = stringResource(id = R.string.confirm_drawing))
                    }
                }

                if (polygonPoints.size > 2) {
                    Button(
                        onClick = {
                            onToggleVisibility()
                            areaShown = true
                        },
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.background,
                            disabledContainerColor = Color(color = 0xFF4CAF50),
                            disabledContentColor = Color(color = 0xFF4CAF50)
                        )
                    ) {
                        Text(text = stringResource(id = R.string.show_area))
                    }
                }

                if (polygonPoints.isNotEmpty()) {
                    Button(
                        onClick = {
                            viewModel.removeLastPoint()
                            onToggleVisibility
                        },
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.onErrorContainer,
                            contentColor = MaterialTheme.colorScheme.background,
                            disabledContainerColor = Color(color = 0xFF4CAF50),
                            disabledContentColor = Color(color = 0xFF4CAF50)
                        )
                    )

                    {
                        Text(text = stringResource(id = R.string.remove_last_point))
                    }
                    Button(
                        onClick = {
                            viewModel.removePoints()
                            onToggleVisibility()
                        },
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,//litt lav kontrsast kanskje bruke .background
                            disabledContainerColor = Color(color = 0xFF4CAF50),
                            disabledContentColor = Color(color = 0xFF4CAF50)
                        )
                    ) {
                        Text(text = stringResource(id = R.string.remove_points))
                    }
                }
            }
            Button(
                onClick = {
                    onCancel
                },
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.tertiary,
                    disabledContainerColor = Color(color = 0xFF4CAF50), //random farge som ikke brukes
                    disabledContentColor = Color(color = 0xFF4CAF50)
                )
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    }

}

@Composable
fun LocationButton(
    locationPermissionGranted: Boolean,
    onAddressDetected: (String) -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    //Current location button
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 150.dp)
        ) {
            Button(
                onClick = {
                    if (locationPermissionGranted && activity != null) {
                        scope.launch {
                            val locationService = LocationService(activity)
                            try {
                                val location = locationService.getCurrentLocation()
                                location?.let {
                                    val address = getAddressFromLocation(context, it)
                                    address?.let {
                                        onAddressDetected(it)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("MapScreen", "Feil ved henting av lokasjon", e)
                            }
                        }
                    }
                },
                modifier = Modifier.size(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = stringResource(id = R.string.current_location)
                )
            }
        }
    }
}


//https://stackoverflow.com/questions/70598043/how-to-use-custom-icon-of-google-maps-marker-in-compose

@Composable
fun MapMarker(
    context: Context,
    title: String,
    snippet: String? = null,
    state: MarkerState,
) {
//    val iconResourceId = R.drawable.location_on_24px //Outlined versjon av den under
    val iconResourceId = R.drawable.location_on_24px_filled //litt rundere
//    val iconResourceId = R.drawable.baseline_location_pin_24 //jeg foretrekker denne men får den ikke til å funkne

    val tintColor = MaterialTheme.colorScheme.tertiary.toArgb()

    val icon =
        bitmapDescriptor(context, iconResourceId, width = 120, height = 120, tint = tintColor)

    if (snippet != null) {
        Marker(
            state = state,
            title = title,
            snippet = snippet,
            icon = icon,
        )
    } else {
        Marker(
            state = state,
            title = title,
            icon = icon,
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