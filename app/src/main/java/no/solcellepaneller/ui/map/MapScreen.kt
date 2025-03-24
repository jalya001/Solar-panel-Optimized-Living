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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import no.solcellepaneller.ui.navigation.TopBar


@Composable
fun MapScreen(viewModel: MapScreenViewModel, navController: NavController) {
    Scaffold(
        topBar = { TopBar(navController){
            viewModel.removePoints()
        } },
    ){ contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            no.solcellepaneller.ui.map.DisplayScreen(viewModel, navController)
        }
    }
}

@Composable
fun DisplayScreen(viewModel: MapScreenViewModel, navController: NavController) {
    val coordinates by viewModel.coordinates.observeAsState()
    var slope by remember { mutableStateOf("") }
    var efficiency by remember { mutableStateOf("") }

    var selectedCoordinates by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = coordinates?.let { LatLng(it.first, coordinates!!.second) }
            ?.let { CameraPosition.fromLatLngZoom(it, 19f) }!!
    }
    val markerState = rememberMarkerState(position = selectedCoordinates ?: LatLng(0.0, 0.0))
    val polygonPoints = viewModel.polygondata
    var ispolygonvisible by remember { mutableStateOf(false) }
    var showPopUp by remember { mutableStateOf(false) }
    val fontsize = 20
    var index: Int by remember { mutableIntStateOf(0) }
    var arealatlong by remember { mutableStateOf<LatLng?>(null) }

    Box(modifier = Modifier.fillMaxWidth()) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = MapType.HYBRID),

            onMapClick = { latLng ->
                if (!ispolygonvisible) {
                    selectedCoordinates = latLng
                    viewModel.addPoint(latLng)
                    index += 1
                }
            }
        )
        {
            polygonPoints.forEach { latLng ->
                Marker(
                    state = rememberMarkerState(position = latLng),
                    title = "Edge: $index"
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

        LaunchedEffect(coordinates) {
            coordinates?.let {
                val newLatLng = LatLng(it.first, it.second)
                selectedCoordinates = newLatLng
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition(newLatLng, 19f, 0f, 0f)
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .zIndex(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LaunchedEffect(coordinates) {
                selectedCoordinates?.let { latLng ->
                    markerState.position = latLng
                    val currentZoom = cameraPositionState.position.zoom
                    val currentBearing = cameraPositionState.position.bearing
                    cameraPositionState.position = CameraPosition(
                        selectedCoordinates!!,  // target
                        currentZoom,            // zoom
                        0f,            // tilt
                        currentBearing          // bearing
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            coordinates?.let {
                selectedCoordinates = LatLng(it.first, it.second)
                if (ispolygonvisible) {
                    Surface(
                        modifier = Modifier.padding(top = 10.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "The area of your roof is: ${
                                    viewModel.calculateAreaOfPolygon(polygonPoints)
                                } m²",
                                style = TextStyle(
                                    fontSize = fontsize.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            )
                            arealatlong?.let {
                                Text(
                                    text = "Latitude: ${it.latitude}",
                                    style = TextStyle(
                                        fontSize = fontsize.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                )
                                Text(
                                    text = "Longitude: ${it.longitude}",
                                    style = TextStyle(
                                        fontSize = fontsize.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            }
                            Text(
                                text = "Efficiency: $efficiency %",
                                style = TextStyle(
                                    fontSize = fontsize.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            )
                            TextField(
                                value = efficiency,
                                onValueChange = { efficiency = it },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = "Slope: $slope°",
                                style = TextStyle(
                                    fontSize = fontsize.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            )
                            TextField(
                                value = slope,
                                onValueChange = { slope = it },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(onClick = {
                                navController.navigate("result")
                            },
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary))
                                 {
                                Text(text = "Go To Result Screen", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

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
                        Button(onClick = {
                            if (polygonPoints.size > 3) {
                                ispolygonvisible = !ispolygonvisible
                                showPopUp = true

                                arealatlong = cameraPositionState.position.target
                            }
                        }, colors = ButtonDefaults.buttonColors(Color(color = 0xFF4CAF50))) {
                            Text(color = Color.Black, text = "Show AREA")
                        }
                        if (polygonPoints.size >= 1) {
                            Button(onClick = {
                                viewModel.removeLastPoint()
                                ispolygonvisible = false
                                index -= 1
                            }, colors = ButtonDefaults.buttonColors(Color.Yellow)) {
                                Text(color = Color.Black, text = "Remove Last Point")
                            }
                            Button(onClick = {
                                viewModel.removePoints()
                                ispolygonvisible = false
                                index = 0
                            }, colors = ButtonDefaults.buttonColors(Color.Red)) {
                                Text(color = Color.Black, text = "Remove Points")
                            }
                        }
                    }
                }
            }
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
