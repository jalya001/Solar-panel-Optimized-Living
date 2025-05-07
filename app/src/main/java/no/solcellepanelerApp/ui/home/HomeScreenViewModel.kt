package no.solcellepanelerApp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import no.solcellepanelerApp.data.weatherdata.WeatherRepository
import no.solcellepanelerApp.model.electricity.Region
import java.time.ZonedDateTime
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import no.solcellepanelerApp.MainActivity
import no.solcellepanelerApp.data.electricitydata.ElectricityPriceRepository
import no.solcellepanelerApp.util.fetchCoordinates
import no.solcellepanelerApp.util.mapLocationToRegion

class HomeScreenViewModel : ViewModel() {
    private val weatherRepository = WeatherRepository.WeatherRepositoryProvider.instance
    private val electricityPriceRepository = ElectricityPriceRepository.ElectricityPriceRepositoryProvider.instance

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _selectedRegion = MutableStateFlow<Region?>(null)
    val selectedRegion: StateFlow<Region?> = _selectedRegion
    private val _currentLocation = MutableStateFlow<Location?>(null)
    //val currentLocation: StateFlow<Location?> = _currentLocation
    private val _locationPermissionGranted = MutableStateFlow(false)
    //val locationPermissionGranted: StateFlow<Boolean> = _locationPermissionGranted
    private val _currentRadiationValue = MutableStateFlow<Double?>(null)
    val currentRadiationValue: StateFlow<Double?> = _currentRadiationValue

    private val _currentTime = MutableStateFlow(ZonedDateTime.now())
    val currentTime: StateFlow<ZonedDateTime> = _currentTime

    init {
        viewModelScope.launch {
            while (true) {
                _currentTime.value = ZonedDateTime.now()
                updateCurrentRadiation(_currentTime.value)
                delay(6_000_000L) // update every now and then
            }
        }
    }

    fun initialize(context: Context) {
        viewModelScope.launch {
            val permissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            _locationPermissionGranted.value = permissionGranted

            if (permissionGranted && context is MainActivity) {
                val location = fetchCoordinates(context)
                _currentLocation.value = location
                _selectedRegion.value = location?.let { mapLocationToRegion(it) } ?: Region.OSLO
            } else {
                _selectedRegion.value = Region.OSLO
            }

            updateCurrentRadiation(_currentTime.value)
        }
    }

    private suspend fun updateCurrentRadiation(time: ZonedDateTime) {
        val location = _currentLocation.value ?: return
        val radiationTimedArray = weatherRepository.rimData.value
        if (radiationTimedArray == null || radiationTimedArray.timestamp < time.minusHours(1)) {
            weatherRepository.fetchRimData(
                location.latitude, location.longitude,"mean(surface_downwelling_shortwave_flux_in_air PT1H)"
            )
        }
        if (weatherRepository.rimData.value == null || weatherRepository.rimData.value!!.data.isEmpty()) {
            _currentRadiationValue.value = null // Error
        } else {
            val hour = _currentTime.value.minusHours(2).hour
            _currentRadiationValue.value = weatherRepository.rimData.value!!.data.getOrNull(hour)?.div(1000.0)

        }
    }
}
