package no.SOL.ui.result

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import no.SOL.data.pricedata.PriceRepository
import no.SOL.data.userdata.UserDataRepository
import no.SOL.data.weatherdata.ApiException
import no.SOL.data.weatherdata.WeatherRepository
import no.SOL.model.reusables.UiState
import no.SOL.ui.handling.NoDataErrorScreen
import no.SOL.ui.handling.PartialDataErrorScreen
import no.SOL.ui.handling.UnexpectedErrorScreen
import no.SOL.ui.handling.UnknownErrorScreen
import java.time.ZonedDateTime

class ResultViewModel : ViewModel() {
    private val weatherRepository = WeatherRepository.WeatherRepositoryProvider.instance
    private val userDataRepository = UserDataRepository.UserDataRepositoryProvider.instance
    private val priceRepository = PriceRepository.PriceRepositoryProvider.instance

    private val coordinatesState = userDataRepository.coordinatesState
    private val areaState = userDataRepository.areaState
    private val angleState = userDataRepository.angleState // Make into int all the way
    private val directionState = userDataRepository.directionState
    private val efficiencyState = userDataRepository.efficiencyState

    //val selectedRegionState = userDataRepository.selectedRegionState
    val height = userDataRepository.height

    private val weatherDataFlow = weatherRepository.weatherData

    private var hasInitialized = false

    fun initialize() {
        if (hasInitialized) return
        hasInitialized = true

        viewModelScope.launch {
            try {
                _uiState.value = UiState.LOADING

                val coordinates =
                    coordinatesState.value ?: throw IllegalStateException("Coordinates missing")
                userDataRepository.fetchHeight()

                Log.d("ResultViewModel coords", coordinatesState.value.toString())
                Log.d("ResultViewModel area", areaState.value.toString())
                Log.d("ResultViewModel angle", angleState.value.toString())
                Log.d("ResultViewModel direction", directionState.value.toString())
                Log.d("ResultViewModel efficiency", efficiencyState.value.toString())
                //Log.d("ResultViewModel region",selectedRegionState.value.toString())
                Log.d("ResultViewModel height", height.value.toString())

                val lat = coordinates.latitude
                val lon = coordinates.longitude

                weatherRepository.getPanelWeatherData(
                    lat,
                    lon,
                    height.value,
                    angleState.value.toInt(),
                    directionState.value.toInt()
                )
                Log.d("ResultViewModel", weatherDataFlow.value.toString())

                if (weatherDataFlow.value!!.isEmpty()) {
                    _errorScreen.value = { NoDataErrorScreen() }
                    _uiState.value = UiState.ERROR
                    return@launch
                } else if (weatherDataFlow.value!!.size != 4) {
                    _errorScreen.value = { PartialDataErrorScreen() }
                    _uiState.value = UiState.ERROR
                    return@launch
                }

                val area = areaState.value
                val efficiency = efficiencyState.value

                priceRepository.updatePrices(ZonedDateTime.now())
                _averagePrice.value = priceRepository.getCurrentRegionsAverage()
                calculateSolarPanelOutput(area, efficiency)
                calculateTemperatureFactors()

                _uiState.value = UiState.SUCCESS

                Log.d("ResultViewModel", "Calculation: ${calculationResults.value}")
            } catch (t: Throwable) {
                Log.e("ResultViewModel", "Error loading panel weather data", t)
                _uiState.value = UiState.ERROR
                _errorScreen.value =
                    (t as? ApiException)?.getErrorScreen() ?: { UnexpectedErrorScreen() }
                return@launch
            }
        }
    }

    private fun calculateTemperatureFactors() {
        _temperatureFactors.value =
            weatherRepository.weatherData.value!!["mean(air_temperature P1M)"]!!
                .map { temp ->
                    1 + (-0.44) * (temp - 25)
                }
    }

    data class MonthlyCalculationResult(
        val adjustedRadiation: List<Double>,
        val monthlyEnergyOutput: List<Double>,
        val monthlyPowerOutput: List<Double>,
        val yearlyEnergyOutput: Double,
    )

    private val _averagePrice = MutableStateFlow<Double?>(null)
    val averagePrice: StateFlow<Double?> = _averagePrice

    private val _temperatureFactors = MutableStateFlow<List<Double>?>(null)
    val temperatureFactors: StateFlow<List<Double>?> = _temperatureFactors

    private val _uiState = MutableStateFlow(UiState.LOADING)
    val uiState: StateFlow<UiState> = _uiState
    private val _errorScreen = MutableStateFlow<@Composable () -> Unit> { UnknownErrorScreen() }
    val errorScreen: StateFlow<@Composable () -> Unit> = _errorScreen

    //private val _frostDataRim = MutableStateFlow<Array<Double>>(emptyArray())
    //val frostDataRim: StateFlow<Array<Double>> = _frostDataRim

    val calculationResults = weatherRepository.calculationResults

    // Default temperature coefficient for solar panels
    private val temperatureCoefficient = -0.44

    private val daysInMonth = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

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
        val snowCoverData = weatherDataFlow.value!!["mean(snow_coverage_type P1M)"]!!
        val airTempData = weatherDataFlow.value!!["mean(air_temperature P1M)"]!!
        val cloudCoverData = weatherDataFlow.value!!["mean(cloud_area_fraction P1M)"]!!
        val radiationData = weatherDataFlow.value!!["mean(PVGIS_radiation P1M)"]!!

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

        calculationResults.value = MonthlyCalculationResult(
            adjustedRadiation = adjustedRadiation,
            monthlyEnergyOutput = monthlyEnergyOutput,
            monthlyPowerOutput = monthlyPowerOutput,
            yearlyEnergyOutput = yearlyEnergyOutput
        )
    }

    /*
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
    */
    fun calculateSavedCO2(energy: Double): Double {
        val norwayEmissionFactor = 0.018 //0.018 kg CO2/kWh
        val norwaySavedCO2 = energy * norwayEmissionFactor

        return norwaySavedCO2
    }
}
