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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    var areaInput by mutableStateOf("")
    var angleInput by mutableStateOf("")
    var directionInput by mutableStateOf("")
    var efficiencyInput by mutableStateOf("")

    var selectedRegion: Region = Region.OSLO


    private val _coordinates = MutableLiveData<Pair<Double, Double>?>()
    val coordinates: LiveData<Pair<Double, Double>?> get() = _coordinates

    private val _height = MutableStateFlow<Double?>(null)
    val height: StateFlow<Double?> = _height


    //could add adress name
    var polygondata = mutableStateListOf<LatLng>()

    fun addPoint(latLng: LatLng) {
        if (polygondata.size < 10) {
            polygondata.add(latLng)

        }
    }

    fun calculateAreaOfPolygon(latLngList: List<LatLng>): Int {
        // Convert LatLng list to a polygon area
        val polygonArea = SphericalUtil.computeArea(latLngList)
        val roundedArea = ceil(polygonArea).toInt()
        return roundedArea
    }

    fun calculateArea(points: List<LatLng>): Double {
        if (polygondata.size < 3) return 0.0 // A polygon must have at least 3 points

        var area = 0.0
        val n = points.size

        for (i in 0 until n) {
            val j = (i + 1) % n
            val lat1 = points[i].latitude
            val lng1 = points[i].longitude
            val lat2 = points[j].latitude
            val lng2 = points[j].longitude

            area += (lng1 * lat2 - lng2 * lat1)
        }


        return abs(area) / 2.0

    }

    fun removePoints() {
        polygondata.clear()
    }

    fun removeLastPoint() {
        if (polygondata.size > 0) {
            polygondata.removeAt(polygondata.size - 1)
        }

    }

    val addressFetchError = mutableStateOf(false)
    val snackbarMessageTrigger = mutableStateOf(0)

    fun fetchCoordinates(address: String) {
        viewModelScope.launch {
            try {
                val result = repository.getCoordinates(address)
                if (result.isNotEmpty()) {
                    val coordinate = result[0]
                    _height.value = repository.getHeight(Pair(coordinate.lat.toDouble(), coordinate.lon.toDouble()))
                    _coordinates.postValue(
                        Pair(
                            coordinate.lat.toDouble(),
                            coordinate.lon.toDouble()
                        )
                    )
                } else {
                    snackbarMessageTrigger.value++
                }
            } catch (e: Exception) {
                Log.e("MyViewModel", "Error fetching coordinates", e)
                snackbarMessageTrigger.value++
            }
        }
    }


    // to change if map is clicked not used
    fun selectLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            _coordinates.postValue(lat to lon)  //  Updates lat/lon
            if (_coordinates.value != null) {
                _height.value = repository.getHeight(_coordinates.value!!)
            }
        }
    }

    
    fun updatePoint(index: Int, newPosition: LatLng) {
        if (index in polygondata.indices) {
            polygondata[index] = newPosition
        }
    }


}


