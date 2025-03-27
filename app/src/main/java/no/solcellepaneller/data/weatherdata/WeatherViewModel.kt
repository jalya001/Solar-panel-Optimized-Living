package no.solcellepaneller.data.weatherdata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import no.solcellepaneller.model.weather.Radiation

class WeatherViewModel(private val repository: WeatherRepository, lat: Double, long: Double, slope: Int) : ViewModel() {
    private val _radiationData = MutableStateFlow<List<Radiation>>(emptyList())
    val radiationData: StateFlow<List<Radiation>> get() = _radiationData

    init {
        fetchRadiationInfo(lat, long, slope)
    }

    private fun fetchRadiationInfo(lat: Double, long: Double, slope: Int) {
        viewModelScope.launch{
            _radiationData.value = repository.getRadiationInfo(lat, long, slope)
        }
    }
}
