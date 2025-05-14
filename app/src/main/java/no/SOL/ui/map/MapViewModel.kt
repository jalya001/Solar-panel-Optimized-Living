package no.SOL.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapUiSettings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import no.SOL.MainActivity
import no.SOL.data.pricedata.PriceRepository
import no.SOL.data.userdata.UserDataRepository
import java.math.BigDecimal
import kotlin.math.ceil
import no.SOL.util.fetchCoordinates as activityToCoordinates

class MapViewModel : ViewModel() {
    private val userDataRepository = UserDataRepository.UserDataRepositoryProvider.instance
    private val priceRepository = PriceRepository.PriceRepositoryProvider.instance

    val coordinatesState = userDataRepository.coordinatesState
    val areaState = userDataRepository.areaState
    val angleState = userDataRepository.angleState
    val directionState = userDataRepository.directionState
    val efficiencyState = userDataRepository.efficiencyState

    val region =
        priceRepository.region // Seems like a waste to import pricerepo just for this, but oh well

    var areaInputText by mutableStateOf("")
        private set

    fun initializeArea() {
        val area = areaState.value
        areaInputText = if (area == 0.0) {
            ""
        } else {
            BigDecimal(area).stripTrailingZeros().toPlainString()
        }
    }

    fun onAreaInputChanged(input: String) {
        areaInputText = input
        input.replace(',', '.').toDoubleOrNull()?.let {
            areaState.value = it
        }
    }

    var address by mutableStateOf("")
    var isPolygonVisible by mutableStateOf(false)
    private val _drawingEnabled = MutableStateFlow(false)
    val drawingEnabled: StateFlow<Boolean> = _drawingEnabled

    var selectedCoordinates by mutableStateOf<LatLng?>(null)

    private var index by mutableIntStateOf(0)
    var showBottomSheet by mutableStateOf(false)
    var showMissingLocationDialog by mutableStateOf(false)
    var currentLocation by mutableStateOf<Location?>(null)
    var locationPermissionGranted by mutableStateOf(false)

    val cameraPositionState = CameraPositionState(
        position = CameraPosition.fromLatLngZoom(LatLng(59.9436145, 10.7182883), 18f)
    )

    val mapUiSettings = MapUiSettings()

    private val _polygonData = mutableStateListOf<LatLng>()
    val polygonData: SnapshotStateList<LatLng> get() = _polygonData

    private val _snackbarMessages = MutableSharedFlow<String>()
    val snackbarMessages = _snackbarMessages.asSharedFlow()

    fun selectLocation(lat: Double, lon: Double) {
        val coordinates = LatLng(lat, lon)
        selectedCoordinates = coordinates
        coordinatesState.value = coordinates
        Log.d("HELLO HELLO", coordinatesState.value.toString())
    }

    fun fetchCoordinates(address: String) {
        viewModelScope.launch {
            try {
                val result = userDataRepository.getCoordinates(address)
                if (result.isNotEmpty()) {
                    coordinatesState.value =
                        LatLng(result.first().lat.toDouble(), result.first().lon.toDouble())
                } else {
                    _snackbarMessages.emit("No results found for the address.")
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error fetching coordinates", e)
                _snackbarMessages.emit("Error fetching coordinates. Try again")
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
        Log.d("MapViewModel","Location permission before: $locationPermissionGranted")
        locationPermissionGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        Log.d("MapViewModel","Location permission after: $locationPermissionGranted")
    }

    fun startDrawing() {
        _drawingEnabled.value = true
        Log.d("drawing is :", drawingEnabled.toString())

        selectedCoordinates = null
        removePoints()
        index = 0
    }

    fun stopDrawing() {
        _drawingEnabled.value = false
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

    fun clearSelection() {
        isPolygonVisible = false
        selectedCoordinates = null
        removePoints()
        index = 0
    }
}
