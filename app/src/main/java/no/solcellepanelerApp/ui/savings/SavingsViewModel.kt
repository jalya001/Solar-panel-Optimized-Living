package no.solcellepanelerApp.ui.savings

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

    private val preConnected = listOf("Fridge", "TV", "Laptop")

    init {
        viewModelScope.launch {
        }
    }

    val devices = listOf(
        "El-Car" to 100.0,
        "Fridge" to 30.0,
        "Heater" to 60.0,
        "Laptop" to 10.0,
        "Washing Machine" to 20.0,
        "TV" to 15.0,
        "Air Conditioner" to 50.0,
        "Microwave" to 25.0,
        "Dishwasher" to 35.0,
        "Vacuum Cleaner" to 8.0
    )

    val deviceIcons = mapOf(
        "Fridge" to R.drawable.kitchen_24px,
        "Washing Machine" to R.drawable.local_laundry_service_24px,
        "TV" to R.drawable.tv_24px,
        "Laptop" to R.drawable.laptop_windows_24px,
        "Air Conditioner" to R.drawable.mode_fan_24px,
        "Heater" to R.drawable.fireplace_24px,
        "Microwave" to R.drawable.microwave_24px,
        "Dishwasher" to R.drawable.dishwasher_24px,
        "El-Car" to R.drawable.directions_car_24px,
        "Vacuum Cleaner" to R.drawable.vacuum_24px,
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
