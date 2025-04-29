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
data class MonthlyCalculationResult(
    val adjustedRadiation: List<Double>,
    val monthlyEnergyOutput: List<Double>,
    val monthlyPowerOutput: List<Double>,
    val yearlyEnergyOutput: Double
)

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

    private val _calculationResults = MutableStateFlow<MonthlyCalculationResult?>(null)
    val calculationResults: StateFlow<MonthlyCalculationResult?> = _calculationResults

    // Default temperature coefficient for solar panels
    private val temperatureCoefficient = -0.44

    private val daysInMonth = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    fun loadWeatherData(
        lat: Double,
        lon: Double,
        height: Double?,
        slope: Int,
        azimuth: Int,
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.LOADING
            val result = repository.getPanelWeatherData(lat, lon, height, slope, azimuth)
            if (result.isSuccess) {
                _weatherData.value = result.getOrNull() ?: emptyMap()
                if (_weatherData.value.isEmpty()) {
                    _errorMessage.value = result.exceptionOrNull()?.message
                        ?: "There is no data on this region. We are sorry."
                    _uiState.value = UiState.ERROR
                } else if (_weatherData.value.size != 4) {
                    _errorMessage.value = result.exceptionOrNull()?.message
                        ?: "Some data missing on this region, and we cannot provide you an estimate. We are sorry."
                    _uiState.value = UiState.ERROR
                } else {
                    _uiState.value = UiState.SUCCESS
                }
            } else {
                _uiState.value = UiState.ERROR
                _errorMessage.value = result.exceptionOrNull()?.message
                    ?: "Unexpected behavior. Please report to developers."
            }
        }
    }

    fun fetchRimData(lat: Double, lon: Double, elements: String) {
        viewModelScope.launch {
            _uiState.value = UiState.LOADING
            _frostDataRim.value = repository.getRimData(lat, lon, elements)
            _uiState.value = UiState.SUCCESS
        }
    }

    fun calculateSolarPanelOutput(panelArea: Double, efficiency: Double) {
        viewModelScope.launch {
            if (_weatherData.value.size != 4) {
                _errorMessage.value = "Insufficient weather data for calculations"
                _uiState.value = UiState.ERROR
                return@launch
            }

            val snowCoverData = _weatherData.value["mean(snow_coverage_type P1M)"] ?: emptyArray()
            val airTempData = _weatherData.value["mean(air_temperature P1M)"] ?: emptyArray()
            val cloudCoverData = _weatherData.value["mean(cloud_area_fraction P1M)"] ?: emptyArray()
            val radiationData = _weatherData.value["mean(PVGIS_radiation P1M)"] ?: emptyArray()

            // Process data and calculate energy output
            val adjustedRadiation = mutableListOf<Double>()
            val monthlyEnergyOutput = radiationData.indices.map { month ->
                adjustedRadiation.add(
                    radiationData[month] *
                            (1 - (cloudCoverData[month].coerceIn(0.0, 8.0) / 8)) *
                            (1 - (snowCoverData[month].coerceIn(0.0, 4.0) / 4))
                )
                val tempFactor = 1 + temperatureCoefficient * (airTempData[month] - 25)
                adjustedRadiation[month] * panelArea * (efficiency / 100.0) * tempFactor
            }

            // Calculate monthly power output (kW)
            val monthlyPowerOutput = monthlyEnergyOutput.mapIndexed { index, energyKWh ->
                val totalHours = daysInMonth[index] * 24 // Total hours in the month
                energyKWh / totalHours // Convert kWh to kW
            }

            // Calculate yearly total energy output
            val yearlyEnergyOutput = monthlyEnergyOutput.sum()

            _calculationResults.value = MonthlyCalculationResult(
                adjustedRadiation = adjustedRadiation,
                monthlyEnergyOutput = monthlyEnergyOutput,
                monthlyPowerOutput = monthlyPowerOutput,
                yearlyEnergyOutput = yearlyEnergyOutput
            )
        }
    }
}
