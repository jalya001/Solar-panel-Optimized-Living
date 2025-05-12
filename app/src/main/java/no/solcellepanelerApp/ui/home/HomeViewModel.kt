package no.solcellepanelerApp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import no.solcellepanelerApp.data.weatherdata.WeatherRepository
import no.solcellepanelerApp.model.price.Region
import java.time.ZonedDateTime
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import no.solcellepanelerApp.MainActivity
import no.solcellepanelerApp.data.pricedata.PriceRepository
import no.solcellepanelerApp.model.reusables.UiState
import no.solcellepanelerApp.model.price.fetchPrices
import no.solcellepanelerApp.util.fetchCoordinates
import no.solcellepanelerApp.util.mapLocationToRegion

class HomeViewModel : ViewModel() {
    private val weatherRepository = WeatherRepository.WeatherRepositoryProvider.instance
    private val priceRepository = PriceRepository.PriceRepositoryProvider.instance


    private val _rimUiState = MutableStateFlow(UiState.LOADING)
    val rimUiState: StateFlow<UiState> = _rimUiState
    private val _priceUiState = MutableStateFlow(UiState.LOADING)
    val priceUiState: StateFlow<UiState> = _priceUiState

    val region = priceRepository.region
    val prices = priceRepository.prices

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _currentLocation = MutableStateFlow<Location?>(null)
    //val currentLocation: StateFlow<Location?> = _currentLocation
    private val _locationPermissionGranted = MutableStateFlow(false)
    //val locationPermissionGranted: StateFlow<Boolean> = _locationPermissionGranted
    private val _currentRadiationValue = MutableStateFlow<Double?>(null)
    val currentRadiationValue: StateFlow<Double?> = _currentRadiationValue

    private val radiationArray = weatherRepository.rimData

    private val _currentTime = MutableStateFlow(ZonedDateTime.now())
    val currentTime: StateFlow<ZonedDateTime> = _currentTime

    init {
        viewModelScope.launch {
            while (true) {
                delay(6_000_000L) // update every now and then
                _currentTime.value = ZonedDateTime.now()
                launch { doFetchPrices() }
                launch { updateCurrentRadiation(_currentTime.value) }
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
                region.value = location?.let { mapLocationToRegion(it) } ?: Region.OSLO
            } else {
                region.value = Region.OSLO
            }

            launch { doFetchPrices() }
            launch { updateCurrentRadiation(_currentTime.value) }
        }
    }

    private suspend fun updateCurrentRadiation(time: ZonedDateTime) {
        val location = _currentLocation.value ?: return

        weatherRepository.updateRimData(
            location.latitude,
            location.longitude,
            "mean(surface_downwelling_shortwave_flux_in_air PT1H)",
            time
        )
        val hour = _currentTime.value.minusHours(2).hour
        _currentRadiationValue.value = if (radiationArray.value?.data.isNullOrEmpty()) {
            _rimUiState.value = UiState.ERROR
            null
        } else {
            _rimUiState.value = UiState.SUCCESS
            radiationArray.value!!.data[hour].div(1000.0)
        }
    }

    private suspend fun doFetchPrices() {
        fetchPrices(_priceUiState, _currentTime.value, priceRepository)
    }
}
