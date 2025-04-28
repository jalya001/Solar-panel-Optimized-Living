package no.solcellepanelerApp.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import no.solcellepanelerApp.data.weatherdata.WeatherRepository

enum class UiState {
    LOADING, SUCCESS, ERROR
}

class WeatherViewModel(
    private val repository: WeatherRepository = WeatherRepository(),
) : ViewModel() {
<<<<<<< HEAD
    private val _weatherData = MutableStateFlow<Map<String, Array<Double>>>(emptyMap())
    val weatherData: StateFlow<Map<String, Array<Double>>> = _weatherData
    private val _uiState = MutableStateFlow(UiState.LOADING)
    val uiState: StateFlow<UiState> = _uiState
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage
=======
    private val _radiationData = MutableStateFlow<List<Radiation>>(emptyList())
    val radiationData: StateFlow<List<Radiation>> = _radiationData
    private val _frostData = MutableStateFlow<Map<String, Array<Double>>>(emptyMap())
    val frostData: StateFlow<Map<String, Array<Double>>> = _frostData

    private val _frostDataRim = MutableStateFlow<Array<Double>>(emptyArray())
    val frostDataRim: StateFlow<Array<Double>> = _frostDataRim

    private val _isloading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isloading
>>>>>>> origin/price+nergy

    //private val lastWeatherData: Map<String, Array<Double>>? = null
    //fun getLastWeatherData(): Map<String, Array<Double>>? = lastWeatherData

<<<<<<< HEAD
    fun loadWeatherData(
=======
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
    fun fetchRimData(lat: Double, lon: Double, elements: String) { // PRODUCTION: Set to private
        viewModelScope.launch {
            _isloading.value = true
            _frostDataRim.value = repository.getRimData(lat, lon, elements)
            _isloading.value = false
        }
    }

    fun fetchWeatherData(
>>>>>>> origin/price+nergy
        lat: Double,
        lon: Double,
        slope: Int,
        azimuth: Int,
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.LOADING
            val result = repository.getPanelWeatherData(lat, lon, slope, azimuth)
            if (result.isSuccess) {
                _weatherData.value = result.getOrNull()?: emptyMap()
                if (_weatherData.value.isEmpty()) {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Empty error"
                    _uiState.value = UiState.ERROR
                } else if (_weatherData.value.size != 4) {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Some elements missing. Guesstimation not implemented."
                    _uiState.value = UiState.ERROR
                } else {
                    _uiState.value = UiState.SUCCESS
                }
            } else {
                _uiState.value = UiState.ERROR
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Unexpected behavior"
            }
        }
    }
}
