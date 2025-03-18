package no.solcellepaneller.ui.weatherStations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import no.solcellepaneller.BottomBar
import no.solcellepaneller.TopBar

@Composable
fun WeatherStationsScreen(navController: NavController) {
    val lastMainScreen="weather_stations'"
    Scaffold(
        topBar = { TopBar { navController.popBackStack() } },
        bottomBar = { BottomBar(navController,lastMainScreen) }
    ){ contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Værstasjon")
            Button(onClick = { navController.navigate("additional_input") }) { Text("Gå til Ekstra Inndata") }
        }
    }
}
