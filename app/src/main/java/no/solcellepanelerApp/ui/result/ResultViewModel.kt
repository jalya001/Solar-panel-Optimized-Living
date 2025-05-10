package no.solcellepanelerApp.ui.result

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import no.solcellepanelerApp.data.mapdata.UserDataRepository
import no.solcellepanelerApp.data.weatherdata.ApiException
import no.solcellepanelerApp.data.weatherdata.WeatherRepository
import no.solcellepanelerApp.ui.handling.NoDataErrorScreen
import no.solcellepanelerApp.ui.handling.PartialDataErrorScreen
import no.solcellepanelerApp.ui.handling.UnexpectedErrorScreen
import no.solcellepanelerApp.ui.handling.UnknownErrorScreen

class ResultViewModel : ViewModel() {
    private val weatherRepository = WeatherRepository.WeatherRepositoryProvider.instance
    private val userDataRepository = UserDataRepository.UserDataRepositoryProvider.instance

    val coordinatesFlow = userDataRepository.coordinatesState.stateFlow
    val areaFlow = userDataRepository.areaState.stateFlow
    val angleFlow = userDataRepository.angleState.stateFlow // Make into int all the way
    val directionFlow = userDataRepository.directionState.stateFlow
    val efficiencyFlow = userDataRepository.efficiencyState.stateFlow
    val selectedRegionFlow = userDataRepository.selectedRegionState.stateFlow
    val heightFlow = userDataRepository.height

    val weatherDataFlow = weatherRepository.weatherData

    init {
        viewModelScope.launch {
            Log.d("HELLO HELLO coords",coordinatesFlow.value.toString())
            Log.d("HELLO HELLO area",areaFlow.value.toString())
            Log.d("HELLO HELLO angle",angleFlow.value.toString())
            Log.d("HELLO HELLO direction",directionFlow.value.toString())
            Log.d("HELLO HELLO efficiency",efficiencyFlow.value.toString())
            Log.d("HELLO HELLO region",selectedRegionFlow.value.toString())
            Log.d("HELLO HELLO height",heightFlow.value.toString())
            if (coordinatesFlow.value != null) {
                userDataRepository.getHeight()
                val lat = coordinatesFlow.value!!.latitude
                val lon = coordinatesFlow.value!!.longitude
                loadWeatherData(lat, lon, heightFlow.value, angleFlow.value.toInt(), directionFlow.value.toInt())
                if (_uiState.value == UiState.SUCCESS) {
                    calculateSolarPanelOutput(areaFlow.value, efficiencyFlow.value)
                    calculateTemperatureFactors()
                } else {
                    _errorScreen.value = { UnexpectedErrorScreen() }
                    _uiState.value = UiState.ERROR
                }
            } else {
               _errorScreen.value = { UnexpectedErrorScreen() }
               _uiState.value = UiState.ERROR
            }
        }
    }

    fun calculateTemperatureFactors() {
        _temperatureFactors.value =
            weatherRepository.weatherData.value!!["mean(air_temperature P1M)"]!!
            .map { temp -> 1 + (-0.44) * (temp - 25)
        }
    }

    enum class UiState {
        LOADING, SUCCESS, ERROR
    }

    data class MonthlyCalculationResult(
        val adjustedRadiation: List<Double>,
        val monthlyEnergyOutput: List<Double>,
        val monthlyPowerOutput: List<Double>,
        val yearlyEnergyOutput: Double
    )

    private val _temperatureFactors = MutableStateFlow<List<Double>?>(null)
    val temperatureFactors: StateFlow<List<Double>?> = _temperatureFactors


    private val _uiState = MutableStateFlow(UiState.LOADING)
    val uiState: StateFlow<UiState> = _uiState
    private val _errorScreen = MutableStateFlow<@Composable () -> Unit> { UnknownErrorScreen() }
    val errorScreen: StateFlow<@Composable () -> Unit> = _errorScreen

    //private val _frostDataRim = MutableStateFlow<Array<Double>>(emptyArray())
    //val frostDataRim: StateFlow<Array<Double>> = _frostDataRim

    private val _calculationResults = MutableStateFlow<MonthlyCalculationResult?>(null)
    val calculationResults: StateFlow<MonthlyCalculationResult?> = _calculationResults

    // Default temperature coefficient for solar panels
    private val temperatureCoefficient = -0.44

    private val daysInMonth = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    private fun loadWeatherData(
        lat: Double,
        lon: Double,
        height: Double?,
        slope: Int,
        azimuth: Int,
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.LOADING
            val result = weatherRepository.getPanelWeatherData(lat, lon, height, slope, azimuth)
            if (result.isSuccess) {
                if (weatherDataFlow.value!!.isEmpty()) {
                    _errorScreen.value = (result.exceptionOrNull() as? ApiException)?.getErrorScreen()?: { NoDataErrorScreen() }
                    _uiState.value = UiState.ERROR
                } else if (weatherDataFlow.value!!.size != 4) {
                    _errorScreen.value = (result.exceptionOrNull() as? ApiException)?.getErrorScreen()?: { PartialDataErrorScreen() }
                    _uiState.value = UiState.ERROR
                } else {
                    _uiState.value = UiState.SUCCESS
                }
            } else {
                _uiState.value = UiState.ERROR
                println(result.exceptionOrNull())
                _errorScreen.value = (result.exceptionOrNull() as? ApiException)?.getErrorScreen()?: { UnexpectedErrorScreen() }
            }
        }
    }

    /*
    fun fetchRimData(lat: Double, lon: Double, elements: String) {
        viewModelScope.launch {
            _uiState.value = UiState.LOADING
            _frostDataRim.value = repository.getRimData(lat, lon, elements)
            _uiState.value = UiState.SUCCESS
        }
    }
    */

    private fun calculateSolarPanelOutput(panelArea: Double, efficiency: Double) {
        viewModelScope.launch {
            if (weatherDataFlow.value!!.size != 4) {
                _errorScreen.value = { PartialDataErrorScreen() }
                _uiState.value = UiState.ERROR
                return@launch
            }

            val snowCoverData = weatherDataFlow.value!!["mean(snow_coverage_type P1M)"] ?: emptyArray()
            val airTempData = weatherDataFlow.value!!["mean(air_temperature P1M)"] ?: emptyArray()
            val cloudCoverData = weatherDataFlow.value!!["mean(cloud_area_fraction P1M)"] ?: emptyArray()
            val radiationData = weatherDataFlow.value!!["mean(PVGIS_radiation P1M)"] ?: emptyArray()

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

    fun calculateMonthlyEnergyOutput(
        avgTemp: List<Double>,
        cloudCover: List<Double>,
        snowCover: List<Double>,
        radiation: List<Double>,
        panelArea: Double,
        efficiency: Double,
        tempCoeff: Double,
    ): List<Double> {
        return radiation.indices.map { month ->
            val adjustedRadiation =
                radiation[month] * (1 - cloudCover[month] / 8) * (1 - snowCover[month] / 4)
            val tempFactor = 1 + tempCoeff * (avgTemp[month] - 25)
            adjustedRadiation * panelArea * (efficiency / 100.0) * tempFactor
        }
    }

    fun calculateSavedCO2(energy: Double): Double {
        val norwayEmissionFactor = 0.03 //0.03 kg CO2/kWh
        val norwaySavedCO2 = energy * norwayEmissionFactor

        return norwaySavedCO2
    }
}
