package no.solcellepanelerApp.ui.map

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import no.solcellepanelerApp.data.mapdata.AddressRepository
import no.solcellepanelerApp.data.mapdata.AddressDataSource
import no.solcellepanelerApp.data.mapdata.ElevationApi
import no.solcellepanelerApp.model.electricity.Region

import kotlin.math.abs
import kotlin.math.ceil

class MapScreenViewModel(
    private val repository: AddressRepository = AddressRepository(AddressDataSource(), ElevationApi()),
) : ViewModel() {
    var areaInput by mutableStateOf("") // These should be a data object
    var angleInput by mutableStateOf("")
    var directionInput by mutableStateOf("")
    var efficiencyInput by mutableStateOf("")
    var selectedRegion: Region = Region.OSLO

    private val _coordinates = MutableLiveData<Pair<Double, Double>?>()
    val coordinates: LiveData<Pair<Double, Double>?> get() = _coordinates
    private val _height = MutableStateFlow<Double?>(null)
    val height: StateFlow<Double?> = _height
    private val _polygonData = mutableStateListOf<LatLng>()
    val polygonData: List<LatLng> get() = _polygonData

    private val _snackbarMessages = MutableSharedFlow<String>()
    val snackbarMessages = _snackbarMessages.asSharedFlow()

    fun addPoint(latLng: LatLng) {
        if (_polygonData.size < 10) {
            _polygonData.add(latLng)
        }
    }

    fun updatePoint(index: Int, newPosition: LatLng) {
        if (index in _polygonData.indices) {
            _polygonData[index] = newPosition
        }
    }

    fun removeLastPoint() {
        if (_polygonData.isNotEmpty()) {
            _polygonData.removeAt(_polygonData.size - 1)
        }
    }

    fun removePoints() {
        _polygonData.clear()
    }

    fun calculateAreaOfPolygon(latLngList: List<LatLng>): Int {
        val polygonArea = SphericalUtil.computeArea(latLngList)
        val roundedArea = ceil(polygonArea).toInt()
        return roundedArea
    }

    fun fetchCoordinates(address: String) {
        viewModelScope.launch {
            try {
                val result = repository.getCoordinates(address)
                if (result.isNotEmpty()) {
                    val coords = result.first()
                    val coordinatePair = coords.lat.toDouble() to coords.lon.toDouble()
                    _height.value = repository.getHeight(coordinatePair)
                    _coordinates.postValue(coordinatePair)
                } else {
                    _snackbarMessages.emit("Adresse ikke funnet, prøv igjen.")
                }
            } catch (e: Exception) {
                Log.e("MapScreenViewModel", "Error fetching coordinates", e)
                _snackbarMessages.emit("Noe gikk galt ved henting av koordinater.")
            }
        }
    }

    // to change if map is clicked not used
    fun selectLocation(lat: Double, lon: Double) {
        val coordinate = lat to lon
        _coordinates.postValue(coordinate)
        viewModelScope.launch {
            _height.value = repository.getHeight(coordinate)
        }
    }
}




