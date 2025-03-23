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
        //would also have onBackClick so that points are deleted when navigating back
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
    var address by remember { mutableStateOf("") }
    val coordinates by viewModel.coordinates.observeAsState()
    var slope by remember { mutableStateOf("") }
    var efficiency by remember { mutableStateOf("") }

    var selectedCoordinates by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = coordinates?.let { LatLng(it.first, coordinates!!.second) }
            ?.let { CameraPosition.fromLatLngZoom(it, 19f) }!!
    }
    val markerState = rememberMarkerState(position = selectedCoordinates ?: LatLng(0.0, 0.0))
    //polygon
    val polygonPoints = viewModel.polygondata


    // button polygon visible
    var ispolygonvisible by remember { mutableStateOf(false) }
    var showPopUp by remember { mutableStateOf(false) }

    val fontsize = 20

    //index markers
    var index: Int by remember { mutableIntStateOf(0) }

    var arealatlong by remember { mutableStateOf<LatLng?>(null) }


    Box(modifier = Modifier.fillMaxWidth()) {
        // test no need to use later Text(text = "her er jeg")
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
            selectedCoordinates?.let { latLng ->
//            Marker(
//            state = markerState,
//            title = "Selected Location",
//            snippet = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}")
            }
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
                .zIndex(1f), // Place controls above the map
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

//            Row(
//                modifier = Modifier
//
//            ) {
//                InputField(
//                    value = address,
//                    onValueChange = { address = it },
//                    label = "Enter Address"
//                )
//
//                Button(modifier = Modifier
//                    .padding(start = 8.dp)
//                    .size(width = 100.dp, height = 50.dp),
//                    onClick = {
//                        viewModel.fetchCoordinates(address)
//                        viewModel.removePoints()
//                        ispolygonvisible = false
//
//
//
//
//                        selectedCoordinates?.let { coordinates ->
//                            cameraPositionState.move(
//                                CameraUpdateFactory.newCameraPosition(
//                                    CameraPosition(
//                                        coordinates,  // target
//                                        20f,  // zoom
//                                        0f,  // tilt
//                                        0f // bearing
//                                    )
//                                )
//                            )
//                        }
//                    }) {
//                    Icon(
//                        imageVector = Icons.Filled.Search, // Set the icon here
//                        contentDescription = "Search Coordinates", // Optional, for accessibility
//                        modifier = Modifier.size(24.dp) // Adjust the icon size if needed
//                    )
//                }
//            }
            LaunchedEffect(coordinates) {
                selectedCoordinates?.let { latLng ->
                    markerState.position = latLng

                    val currentZoom = cameraPositionState.position.zoom
                    //val currentTilt= cameraPositionState.position.tilt
                    val currentBearing = cameraPositionState.position.bearing


                    /* cameraPositionState.position = CameraPosition.fromLatLngZoom(selectedCoordinates!!, currentZoom) */

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

                Surface(
                    modifier = Modifier,
                    color = Color.White.copy(alpha = 0.3f), // Light background color for the surface
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Latitude: ${it.first}",
                            style = TextStyle(fontSize = fontsize.sp)
                        )


                        Text(
                            "Longitude: ${it.second}",
                            style = TextStyle(fontSize = fontsize.sp)
                        )
                        Text(text = "MAIN MAP SCREEN", style = TextStyle(fontSize = 50.sp), color = Color.Red)
                    }
                }




                if (ispolygonvisible) {

                    Surface(
                        modifier = Modifier.padding(top = 10.dp),
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(10.dp)

                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp) // Increased space between elements
                        ) {
                            Text(
                                text = "The area of your roof is: ${
                                    viewModel.calculateAreaOfPolygon(
                                        polygonPoints
                                    )
                                } m²",
                                style = TextStyle(
                                    fontSize = fontsize.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50) // Green color for the area text
                                )
                            )

                            arealatlong?.let {
                                Text(
                                    text = "Latitude: ${it.latitude}",
                                    style = TextStyle(
                                        fontSize = fontsize.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF2196F3) // Blue color for latitude
                                    )
                                )
                                Text(
                                    text = "Longitude: ${it.longitude}",
                                    style = TextStyle(
                                        fontSize = fontsize.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF2196F3) // Blue color for longitude
                                    )
                                )
                            }

                            Text(
                                text = "Efficiency: $efficiency %",
                                style = TextStyle(
                                    fontSize = fontsize.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF9C27B0) // Purple color for efficiency
                                )
                            )

                            Text(
                                text = "Slope: $slope°",
                                style = TextStyle(
                                    fontSize = fontsize.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF9C27B0) // Purple color for slope
                                )
                            )
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
                        Button(onClick = {
//                            idk hva som skjer her

//                            if (ispolygonvisible) {
//                                Navigate back to the Hello screen
//                                navController.navigate("result")
//                            }

                            navController.navigate("result")

                        }) {
                            Text(text = "Go To Result Screen")
                        }
                    }
                }
            }

        }
        if (showPopUp) {
            Dialog(onDismissRequest = { showPopUp = false }) {
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Column {
                        var temporaryslope by remember { mutableStateOf(slope) }
                        var temporaryefficency by remember { mutableStateOf(efficiency) }
                        Text(text = "Enter slope")
                        no.solcellepaneller.ui.map.InputField(
                            value = temporaryslope,
                            onValueChange = { temporaryslope = it },
                            label = "Enter Roof Slope (°)"
                        )
                        Text(text = "Enter efficiency")
                        no.solcellepaneller.ui.map.InputField(
                            value = temporaryefficency,
                            onValueChange = { temporaryefficency = it },
                            label = "Enter Panel Efficiency (%)"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row {
                            Button(onClick = { showPopUp = false }) { Text("Cancel") }
                        }
                        Button(onClick = {
                            showPopUp = false
                            slope = temporaryslope
                            efficiency = temporaryefficency
                        }) { Text("Save") }
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


