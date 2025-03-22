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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState
import no.solcellepaneller.ui.navigation.TopBar

@Composable
fun MapScreenSimple(viewModel: MapScreenSimpleViewModel, navController: NavController) {
    Scaffold(
        topBar = { TopBar(navController) },
    ){ contentPadding ->
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
fun DisplayScreen(viewModel: MapScreenSimpleViewModel, navController: NavController) {
    var address by remember { mutableStateOf("") }
    val coordinates by viewModel.coordinates.observeAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(2.804314, 45.175009), 20f)
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = MapType.HYBRID),
            onMapClick = { latLng ->
                viewModel.selectLocation(latLng.latitude, latLng.longitude)
            }
        )

        LaunchedEffect(cameraPositionState.position) {
            val newPosition = cameraPositionState.position.target
            viewModel.selectLocation(newPosition.latitude, newPosition.longitude)
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
                    label = "Enter Address"
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
                        contentDescription = "Search Coordinates",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            coordinates?.let {
                Surface(
                    modifier = Modifier.padding(top = 16.dp),
                    color = Color.White.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "Latitude: ${it.first}", style = TextStyle(fontSize = 20.sp))
                        Text(text = "Longitude: ${it.second}", style = TextStyle(fontSize = 20.sp))
                    }
                }

                LaunchedEffect(it) {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        LatLng(it.first, it.second),
                        cameraPositionState.position.zoom
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("weather_stations") }
            ) {
                Text(text = "Gå til værstasjoner")
            }
        }
    }
}

