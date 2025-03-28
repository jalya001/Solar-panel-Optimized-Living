package no.solcellepaneller.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import no.solcellepaneller.ui.navigation.AdditionalInputBottomSheet
import no.solcellepaneller.ui.navigation.TopBar
import no.solcellepaneller.R
import no.solcellepaneller.ui.navigation.HelpBottomSheet

@Composable
fun MapScreen(viewModel: MapScreenViewModel, navController: NavController) {
    Scaffold(
        topBar = { TopBar(navController) },
    ) { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DisplayScreen(viewModel, navController)
        }
    }
}

@Composable
fun DisplayScreen(viewModel: MapScreenViewModel, navController: NavController) {
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
                        fillColor = Color.Green.copy(0.3f),
                        strokeColor = Color.Green,
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
                                CameraPosition(newLatLng, cameraPositionState.position.zoom, 0f, cameraPositionState.position.bearing)
                            )
                        )
                    }
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
            Row {
                InputField(
                    value = address,
                    onValueChange = { address = it },
                    label = stringResource(id = R.string.enter_address)
                )

                Button(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(width = 100.dp, height = 50.dp),
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

            Spacer(modifier = Modifier.height(16.dp))

            val coroutineScope = rememberCoroutineScope()

            BekreftLokasjon(
                onClick = {
                    if (coordinates!=null){
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
                        )}
                }else{
                        showMissingLocationDialog= true
                    }
                }
            )

            if (showMissingLocationDialog) {
                LocationNotSelectedDialog(
                    coordinates = coordinates,
                    onDismiss = { showMissingLocationDialog = false },
                    onHelpClick = {
                        showHelpSheet = true
                        showMissingLocationDialog = false
                    }
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
                }, coordinates=coordinates, area=area, navController = navController, viewModel = viewModel
            )
            HelpBottomSheet( //kan evt vise til ulike hjelp skjermer
                visible = showHelpSheet,
                onDismiss = { showHelpSheet = false }
            )
            if(drawingEnabled){
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
                        areaShown=true
                        }
                    , colors = ButtonDefaults.buttonColors(Color(color = 0xFF4CAF50))) {
                        Text(text = stringResource(id = R.string.show_area))
                        }

                    if (areaShown && ispolygonvisible){
                    Button(onClick = {
                        showBottomSheet=true
                    }){
                        Text(text = stringResource(id = R.string.confirm_drawing))
                    }}
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
                        }, colors = ButtonDefaults.buttonColors(Color.Red)) {
                            Text(text = stringResource(id = R.string.remove_points))
                        }
                    }
                }

            }}
        }
    }

}

@Composable
fun InputField(value: String, onValueChange: (String) -> Unit, label: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) })
}

@Composable
fun BekreftLokasjon( //må huske å endre navn på funksjoner ogsånt til engelsk
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Text(stringResource(id = R.string.confirm_location))
    }
}

@Composable
fun LocationNotSelectedDialog(
    coordinates: Pair<Double, Double>?,
    onDismiss: () -> Unit,
    onHelpClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(coordinates == null) }

    if (showDialog && coordinates == null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(stringResource(id = R.string.no_location_title))
            },
            text = {
                Text(stringResource(id = R.string.no_location_message))
            },
            confirmButton = {
                Button(
                    onClick = onHelpClick
                ) {
                    Text(stringResource(id = R.string.need_help))
                }
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

