package no.SOL.ui.savings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import no.SOL.R
import no.SOL.data.weatherdata.WeatherRepository

class SavingsViewModel : ViewModel() {
    private val weatherRepository = WeatherRepository.WeatherRepositoryProvider.instance

    val calculationResults = weatherRepository.calculationResults
    val weatherDataFlow = weatherRepository.weatherData

    private val _connectedDevices = MutableStateFlow<MutableMap<Int, Double>>(mutableMapOf())
    val connectedDevices: StateFlow<Map<Int, Double>> = _connectedDevices

    private val _currentEnergy = MutableStateFlow(0.0)
    val currentEnergy: StateFlow<Double> = _currentEnergy

    private val _savings = MutableStateFlow(0.0)
    val savings: StateFlow<Double> = _savings

    private val preConnected = listOf(
        R.string.fridge,
        R.string.tv,
        R.string.laptop,
        R.string.microwave,
        R.string.dishwasher,
        R.string.washing_machine,
        R.string.vacuum_cleaner
    )

    init {
        viewModelScope.launch {
        }
    }

    val devices = listOf(
        R.string.el_car to 100.0,
        R.string.fridge to 30.0,
        R.string.heater to 60.0,
        R.string.laptop to 10.0,
        R.string.washing_machine to 20.0,
        R.string.tv to 15.0,
        R.string.air_conditioner to 50.0,
        R.string.microwave to 25.0,
        R.string.dishwasher to 35.0,
        R.string.vacuum_cleaner to 8.0
    )

    val deviceIcons = mapOf(
        R.string.fridge to R.drawable.kitchen_24px,
        R.string.washing_machine to R.drawable.local_laundry_service_24px,
        R.string.tv to R.drawable.tv_24px,
        R.string.laptop to R.drawable.laptop_windows_24px,
        R.string.air_conditioner to R.drawable.mode_fan_24px,
        R.string.heater to R.drawable.fireplace_24px,
        R.string.microwave to R.drawable.microwave_24px,
        R.string.dishwasher to R.drawable.dishwasher_24px,
        R.string.el_car to R.drawable.directions_car_24px,
        R.string.vacuum_cleaner to R.drawable.vacuum_24px,
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

    fun toggleDevice(name: Int) {
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
