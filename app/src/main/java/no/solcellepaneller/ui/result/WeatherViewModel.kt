package no.solcellepaneller.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import no.solcellepaneller.data.weatherdata.WeatherRepository
import no.solcellepaneller.model.weather.Radiation

class WeatherViewModel(
    private val repository: WeatherRepository = WeatherRepository()
) : ViewModel() {
    private val _radiationData = MutableStateFlow<List<Radiation>>(emptyList())
    val radiationData: StateFlow<List<Radiation>> = _radiationData
    private val _frostData = MutableStateFlow<Map<String, Array<Double>>>(emptyMap())
    val frostData: StateFlow<Map<String, Array<Double>>> = _frostData
    private val _isloading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isloading

    fun fetchRadiationInfo(lat: Double, long: Double, slope: Int, azimuth: Int) { // PRODUCTION: Set to private
        viewModelScope.launch{

            _radiationData.value = repository.getRadiationInfo(lat, long, slope, azimuth)

        }
    }

    fun fetchFrostData(lat: Double, lon: Double, elements: List<String>) { // PRODUCTION: Set to private
        viewModelScope.launch {
            _isloading.value = true
            _frostData.value = repository.getFrostData(lat, lon, elements)
            _isloading.value = false
        }


    }

    fun fetchWeatherData(lat: Double, lon: Double, slope: Int, azimuth: Int, elements: List<String>) {
        viewModelScope.launch {
            launch { fetchRadiationInfo(lat, lon, slope, azimuth) }
            launch { fetchFrostData(lat, lon, elements) }
        }
    }
}
