package no.solcellepanelerApp.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
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
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
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
import kotlinx.coroutines.launch
import no.solcellepanelerApp.MainActivity
import no.solcellepanelerApp.R
import no.solcellepanelerApp.ui.font.FontSizeState
import no.solcellepanelerApp.ui.navigation.HelpBottomSheet
import no.solcellepanelerApp.ui.onboarding.OnboardingUtils
import no.solcellepanelerApp.ui.navigation.AppScaffoldController
import no.solcellepanelerApp.ui.reusables.SimpleTutorialOverlay
import no.solcellepanelerApp.ui.theme.darkGrey
import no.solcellepanelerApp.ui.theme.lightBlue
import no.solcellepanelerApp.ui.theme.lightGrey
import no.solcellepanelerApp.ui.theme.orange
import no.solcellepanelerApp.util.RequestLocationPermission
import kotlin.math.max


@Composable
fun MapScreen(
    navController: NavController,
    appScaffoldController: AppScaffoldController,
    contentPadding: PaddingValues,
    viewModel: MapViewModel = viewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.snackbarMessages.collect { message ->
            appScaffoldController.showSnackbar(message)
        }
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
            onStartDrawing = { appScaffoldController.enableOverlay("draw") }
        )
    }
}

@Composable
fun DisplayScreen(
    viewModel: MapViewModel,
    navController: NavController,
    onStartDrawing: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? MainActivity

    val coordinates by viewModel.coordinatesState.stateFlow.collectAsState() // This is for later calculation
    val selectedCoordinates =
        viewModel.selectedCoordinates // This is for onscreen handling stuff idk
    val polygonData = viewModel.polygonData
    Log.d("HELLO HELLO", "UI Polygon has ${polygonData.size} points")
    val isPolygonVisible = viewModel.isPolygonVisible
    val drawingEnabled by viewModel.drawingEnabled.collectAsState()
    Log.d("HELLO HELLO drawing from screen is :", drawingEnabled.toString())
    val showBottomSheet = viewModel.showBottomSheet
    val showMissingLocationDialog = viewModel.showMissingLocationDialog
    val locationPermissionGranted = viewModel.locationPermissionGranted
    val markerState = rememberMarkerState(position = selectedCoordinates ?: LatLng(0.0, 0.0))

    val cameraPositionState = viewModel.cameraPositionState
    val mapUiSettings = viewModel.mapUiSettings

    val coroutineScope = rememberCoroutineScope()

    RequestLocationPermission { region ->
        viewModel.region.value = region
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
                            CameraUpdateFactory.newLatLngZoom(
                                latLng,
                                cameraPositionState.position.zoom
                            )
                        )
                    }
                }
            }
        ) {
            if (!drawingEnabled) {
                Log.d("HELLO HELLO", "drawing not")
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
                Log.d("HELLO HELLO", "YES DRAW")
                polygonData.forEachIndexed { index, point ->
                    val pointMarkerState = rememberMarkerState(position = point)

                    LaunchedEffect(pointMarkerState) {
                        snapshotFlow { pointMarkerState.position }
                            .collect { newPosition -> viewModel.updatePoint(index, newPosition) }
                    }

                    MapMarker(
                        state = pointMarkerState,
                        title = "${stringResource(id = R.string.point)} ${index + 1}",
                        draggable = true,
                        displayLabel = true,
                        context = context
                    )
                }

                if (isPolygonVisible && polygonData.isNotEmpty()) {
                    Polygon(
                        points = polygonData,
                        fillColor = lightBlue.copy(0.6f),
                        strokeColor = lightBlue,
                        strokeWidth = 5f
                    )
                }
            }
        }

        LaunchedEffect(coordinates) {
            coordinates?.let { coordinate ->
                val lat = coordinate.latitude
                val lon = coordinate.longitude
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
            navController = navController,
            selectedCoordinates = selectedCoordinates,
            coordinates = coordinates,
            polygonData = polygonData,
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
            areaText = viewModel.calculateAreaOfPolygon(polygonData).toString(),
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
            onToggleBottomSheet = { viewModel.showBottomSheet = true },
            onStartDrawing = onStartDrawing,
        )

        AdditionalInputBottomSheet(
            visible = showBottomSheet,
            onDismiss = { viewModel.showBottomSheet = false },
            navController = navController,
            viewModel = viewModel,
        )
    }
}

@Composable
fun ControlsColumn(
    navController: NavController,
    selectedCoordinates: LatLng?,
    coordinates: LatLng?,
    polygonData: List<LatLng>,
    isPolygonVisible: Boolean,
    onConfirmLocation: () -> Unit,
    areaText: String,
    onMapUseLocationClick: () -> Unit,
    locationPermissionGranted: Boolean,
    drawingEnabled: Boolean,
    showMissingLocationDialog: Boolean,
    onDismissDialog: () -> Unit,
    onTogglePolygonVisibility: () -> Unit,
    viewModel: MapViewModel,
    onToggleBottomSheet: () -> Unit,
    onStartDrawing: () -> Unit,
) {
    var showDrawHelp by remember { mutableStateOf(false) }
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
            LaunchedEffect(key1 = drawingEnabled) {
                if (drawingEnabled) onStartDrawing()
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                ) {
                    HelpDrawingDialog(
                        navController = navController,
                        showHelp = showDrawHelp,
                        onDismiss = { showDrawHelp = false }
                    )
                    Button(
                        onClick = {
                            showDrawHelp = true
                        },
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = darkGrey,
                            disabledContentColor = lightGrey,
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.help_24px),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                DrawingControls(
                    polygonData = polygonData,
                    viewModel = viewModel,
                    isPolygonVisible = isPolygonVisible,
                    toggleBottomSheet = onToggleBottomSheet,
                    onToggleVisibility = onTogglePolygonVisibility,
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
}

@Composable
fun SearchBar(
    address: String,
    onAddressChange: (String) -> Unit,
    onSearch: () -> Unit,
    viewModel: MapViewModel,
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
    viewModel: MapViewModel,
    address: String,
) {
    OutlinedTextField(
        value = value,
        textStyle = TextStyle(fontFamily = FontFamily.Default),
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
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
        )
    )
}

@Composable
fun LocationNotSelectedDialog(
    coordinates: LatLng?,
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
                    Text(
                        stringResource(id = R.string.no_location_title),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                text = {
                    Text(
                        stringResource(id = R.string.no_location_message),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                confirmButton = {

                },
                dismissButton = {
                    Button(
                        onClick = onDismiss
                    ) {
                        Text(
                            stringResource(id = R.string.dismiss),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun HelpDrawingDialog(
    navController: NavController,
    showHelp: Boolean,
    onDismiss: () -> Unit,
) {
    if (showHelp) {
        HelpBottomSheet(
            navController = navController,
            visible = true,
            onDismiss = onDismiss,
            expandSection = "draw"
        )
    }
}


@Composable
private fun DrawingControls(
    polygonData: List<LatLng>,
    viewModel: MapViewModel,
    isPolygonVisible: Boolean,
    onToggleVisibility: () -> Unit,
    toggleBottomSheet: () -> Unit,
) {

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            Box(
                modifier = Modifier
                    .width(270.dp)
                    .height(200.dp)
            ) {

                if (isPolygonVisible) {
                    DrawingControlButton(
                        modifier = Modifier.align(Alignment.TopEnd),
                        icon = Icons.Filled.CheckCircle,
                        label = stringResource(R.string.confirm_drawing),
                        onClick = {
                            viewModel.areaState.value =
                                viewModel.calculateAreaOfPolygon(polygonData).toDouble()
                            viewModel.stopDrawing()
                            viewModel.togglePolygonVisibility()
                            viewModel.removePoints()
                            toggleBottomSheet()
                        },
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                }

                if (polygonData.size >= 3) {
                    DrawingControlButton(
                        modifier = Modifier.align(Alignment.TopStart),
                        icon = if (!isPolygonVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        label = stringResource(
                            if (!isPolygonVisible) R.string.show_area else R.string.hide_area
                        ),
                        onClick = onToggleVisibility,
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                }

                if (polygonData.size >= 2) {
                    DrawingControlButton(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        icon = Icons.Filled.DeleteForever,
                        label = stringResource(R.string.remove_points),
                        onClick = {
                            viewModel.clearSelection()
                        },
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                if (polygonData.isNotEmpty()) {
                    DrawingControlButton(
                        modifier = Modifier.align(Alignment.BottomStart),
                        icon = Icons.AutoMirrored.Filled.Undo,
                        label = stringResource(R.string.remove_last_point),
                        onClick = { viewModel.removeLastPoint() },
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawingControlButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
) {
    Card(
        modifier = modifier
            .width(130.dp)
            .clickable { onClick() },
        colors = CardColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.padding(top = 6.dp),
                tint = contentColor
            )
            Text(
                text = label,
                modifier = Modifier
                    .padding(top = 2.dp, start = 10.dp, end = 10.dp, bottom = 10.dp)
                    .width(130.dp),
                textAlign = TextAlign.Center,
                color = contentColor
            )
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
    displayLabel: Boolean = false,
) {
    val icon = remember(title) {
        val iconBitmap = createLabelledMarkerBitmap(context, title, displayLabel)
        BitmapDescriptorFactory.fromBitmap(iconBitmap)
    }

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

fun createLabelledMarkerBitmap(
    context: Context,
    label: String,
    displayLabel: Boolean = false,
): Bitmap {
    val iconSize = 120
    val padding = 20

    if (displayLabel) {
        val textPaint = Paint().apply {
            color = orange.toArgb() // <-- Your orange
            textSize = 50f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        val bgPaint = Paint().apply {
            color = Color.Black.copy(alpha = 0.6f).toArgb() // light orange background
            isAntiAlias = true
        }

        val fontMetrics = textPaint.fontMetrics
        val textHeight = (fontMetrics.descent - fontMetrics.ascent).toInt()
        val baseline = -fontMetrics.ascent

        val labelPadding = 16
        val labelWidth = max(
            textPaint.measureText(label).toInt() + labelPadding * 2,
            iconSize
        )
        val height = textHeight + iconSize + padding + labelPadding

        val bitmap = Bitmap.createBitmap(labelWidth, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw background behind label
        val rect = RectF(
            0f,
            0f,
            labelWidth.toFloat(),
            textHeight + labelPadding.toFloat()
        )
        canvas.drawRoundRect(rect, 20f, 20f, bgPaint)

        // Draw the label text
        canvas.drawText(
            label,
            labelWidth / 2f,
            baseline + labelPadding / 2f,
            textPaint
        )

        // Draw the marker icon
        val drawable = ContextCompat.getDrawable(context, R.drawable.baseline_location_pin_24)
        drawable?.setTint(orange.toArgb()) // <-- Your orange again
        drawable?.setBounds(
            (labelWidth - iconSize) / 2,
            textHeight + labelPadding + padding,
            (labelWidth + iconSize) / 2,
            textHeight + iconSize + labelPadding + padding
        )
        drawable?.draw(canvas)

        return bitmap
    } else {
        val bitmap = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val drawable = ContextCompat.getDrawable(context, R.drawable.baseline_location_pin_24)
        drawable?.setTint(orange.toArgb())
        drawable?.setBounds(0, 0, iconSize, iconSize)
        drawable?.draw(canvas)

        return bitmap
    }
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

