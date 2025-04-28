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
    private val _weatherData = MutableStateFlow<Map<String, Array<Double>>>(emptyMap())
    val weatherData: StateFlow<Map<String, Array<Double>>> = _weatherData
    private val _uiState = MutableStateFlow(UiState.LOADING)
    val uiState: StateFlow<UiState> = _uiState
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _frostDataRim = MutableStateFlow<Array<Double>>(emptyArray())
    val frostDataRim: StateFlow<Array<Double>> = _frostDataRim

    //private val lastWeatherData: Map<String, Array<Double>>? = null
    //fun getLastWeatherData(): Map<String, Array<Double>>? = lastWeatherData

    fun loadWeatherData(
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

    fun fetchRimData(lat: Double, lon: Double, elements: String) { // PRODUCTION: Set to private
        viewModelScope.launch {
            _uiState.value = UiState.LOADING
            _frostDataRim.value = repository.getRimData(lat, lon, elements)
            _uiState.value = UiState.SUCCESS
        }
    }
}