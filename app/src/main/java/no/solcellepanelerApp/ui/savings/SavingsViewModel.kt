package no.solcellepanelerApp.ui.savings

import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import no.solcellepanelerApp.ui.result.ResultViewModel.MonthlyCalculationResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.solcellepanelerApp.R
import no.solcellepanelerApp.data.weatherdata.WeatherRepository

class SavingsViewModel : ViewModel() {
    private val weatherRepository = WeatherRepository.WeatherRepositoryProvider.instance

    val calculationResults = weatherRepository.calculationResults
    val weatherDataFlow = weatherRepository.weatherData

    private val _connectedDevices = MutableStateFlow<MutableMap<String, Double>>(mutableMapOf())
    val connectedDevices: StateFlow<Map<String, Double>> = _connectedDevices

    private val _currentEnergy = MutableStateFlow(0.0)
    val currentEnergy: StateFlow<Double> = _currentEnergy

    private val _savings = MutableStateFlow(0.0)
    val savings: StateFlow<Double> = _savings

    private val preConnected = listOf(
        stringResource(R.string.fridge),
        stringResource(R.string.tv),
        stringResource(R.string.laptop),
        stringResource(R.string.microwave),
        stringResource(R.string.dishwasher),
        stringResource(R.string.washing_machine),
        stringResource(R.string.vacuum_cleaner)
    )

    init {
        viewModelScope.launch {
        }
    }

    val devices = listOf(
        stringResource(R.string.el_car) to 100.0,
        stringResource(R.string.fridge) to 30.0,
        stringResource(R.string.heater) to 60.0,
        stringResource(R.string.laptop) to 10.0,
        stringResource(R.string.washing_machine) to 20.0,
        stringResource(R.string.tv) to 15.0,
        stringResource(R.string.air_conditioner) to 50.0,
        stringResource(R.string.microwave) to 25.0,
        stringResource(R.string.dishwasher) to 35.0,
        stringResource(R.string.vacuum_cleaner) to 8.0
    )

    val deviceIcons = mapOf(
        stringResource(R.string.fridge) to R.drawable.kitchen_24px,
        stringResource(R.string.washing_machine) to R.drawable.local_laundry_service_24px,
        stringResource(R.string.tv) to R.drawable.tv_24px,
        stringResource(R.string.laptop) to R.drawable.laptop_windows_24px,
        stringResource(R.string.air_conditioner) to R.drawable.mode_fan_24px,
        stringResource(R.string.heater) to R.drawable.fireplace_24px,
        stringResource(R.string.microwave) to R.drawable.microwave_24px,
        stringResource(R.string.dishwasher) to R.drawable.dishwasher_24px,
        stringResource(R.string.el_car) to R.drawable.directions_car_24px,
        stringResource(R.string.vacuum_cleaner) to R.drawable.vacuum_24px,
    )

    fun initialize(energyProduced: Double, energyPrice: Double) {
        val initialConnected = devices
            .filter { it.first in preConnected }
            .associate { it.first to it.second }
            .toMutableMap()

        _connectedDevices.value = initialConnected
        _currentEnergy.value = energyProduced - initialConnected.values.sum()
        _savings.value = energyProduced * energyPrice
    }

    fun toggleDevice(name: String) {
        val value = devices.find { it.first == name }?.second ?: return
        val currentMap = _connectedDevices.value.toMutableMap()

        if (currentMap.containsKey(name)) {
            currentMap.remove(name)
            _currentEnergy.value += value
        } else {
            currentMap[name] = value
            _currentEnergy.value -= value
        }

        _connectedDevices.value = currentMap
    }
}
