package no.solcellepanelerApp.ui.map

import android.Manifest
import no.solcellepanelerApp.MainActivity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import no.solcellepanelerApp.data.mapdata.UserDataRepository
import no.solcellepanelerApp.data.weatherdata.WeatherRepository
import no.solcellepanelerApp.model.electricity.Region

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapUiSettings
import kotlin.math.abs
import kotlin.math.ceil
import no.solcellepanelerApp.util.fetchCoordinates as activityToCoordinates

class MapScreenViewModel : ViewModel() {
    private val userDataRepository = UserDataRepository.UserDataRepositoryProvider.instance
    private val weatherRepository = WeatherRepository.WeatherRepositoryProvider.instance

    var areaInput by mutableStateOf("")
    var angleInput by mutableStateOf("")
    var directionInput by mutableStateOf("")
    var efficiencyInput by mutableStateOf("")
    var selectedRegion: Region = Region.OSLO

    var selectedCoordinates by mutableStateOf<LatLng?>(null)
    var address by mutableStateOf("")
    var isPolygonVisible by mutableStateOf(false)
    var drawingEnabled by mutableStateOf(false)
    var index by mutableIntStateOf(0)
    var showBottomSheet by mutableStateOf(false)
    var showMissingLocationDialog by mutableStateOf(false)
    var currentLocation by mutableStateOf<Location?>(null)
    var locationPermissionGranted by mutableStateOf(false)

    val cameraPositionState = CameraPositionState(
        position = CameraPosition.fromLatLngZoom(LatLng(59.9436145, 10.7182883), 18f)
    )

    val mapUiSettings = MapUiSettings()

    private val _coordinates = MutableLiveData<Pair<Double, Double>?>()
    val coordinates: LiveData<Pair<Double, Double>?> get() = _coordinates

    private val _height = MutableStateFlow<Double?>(null)
    val height: StateFlow<Double?> = _height

    private val _polygonData = mutableStateListOf<LatLng>()
    val polygonData: List<LatLng> get() = _polygonData

    private val _snackbarMessages = MutableSharedFlow<String>()
    val snackbarMessages = _snackbarMessages.asSharedFlow()


    fun selectLocation(lat: Double, lon: Double) {
        val coordinate = lat to lon
        _coordinates.postValue(coordinate)
        selectedCoordinates = LatLng(lat, lon)
        viewModelScope.launch {
            _height.value = userDataRepository.getHeight(coordinate)
        }
    }

    fun fetchCoordinates(address: String) {
        viewModelScope.launch {
            try {
                val result = userDataRepository.getCoordinates(address)
                if (result.isNotEmpty()) {
                    val coords = result.first()
                    val coordinatePair = coords.lat.toDouble() to coords.lon.toDouble()
                    _height.value = userDataRepository.getHeight(coordinatePair)
                    _coordinates.postValue(coordinatePair)
                    selectedCoordinates = LatLng(coordinatePair.first, coordinatePair.second)
                } else {
                    _snackbarMessages.emit("Adresse ikke funnet, prøv igjen.")
                }
            } catch (e: Exception) {
                Log.e("MapScreenViewModel", "Error fetching coordinates", e)
                _snackbarMessages.emit("Noe gikk galt ved henting av koordinater.")
            }
        }
    }

    fun fetchCurrentLocation(activity: MainActivity) {
        viewModelScope.launch {
            val location = activityToCoordinates(activity)
            currentLocation = location
        }
    }

    fun checkLocationPermission(context: Context) {
        locationPermissionGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun startDrawing() {
        drawingEnabled = true
        selectedCoordinates = null
        removePoints()
        index = 0
    }

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
        return ceil(polygonArea).toInt()
    }

    fun togglePolygonVisibility() {
        isPolygonVisible = !isPolygonVisible
    }

    fun toggleBottomSheet(visible: Boolean) {
        showBottomSheet = visible
    }

    fun showMissingLocation(show: Boolean) {
        showMissingLocationDialog = show
    }

    fun clearSelection() {
        selectedCoordinates = null
        removePoints()
        index = 0
    }
}
