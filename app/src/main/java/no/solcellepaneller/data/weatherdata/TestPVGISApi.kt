package no.solcellepaneller.data.weatherdata

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import android.util.Log
import androidx.compose.runtime.LaunchedEffect

@Composable
fun StartApiCall(viewModel: WeatherViewModel) {
    val radiationData = viewModel.radiationData.collectAsState().value

    LaunchedEffect(radiationData) {
        Log.d("API_RESPONSE", "Radiation data: $radiationData")
    }
}
