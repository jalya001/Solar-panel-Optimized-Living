package no.solcellepaneller.ui.mapSimple


import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import no.solcellepaneller.data.mapdata.AddressRepository
import no.solcellepaneller.data.mapdata.AdressDataSource
import kotlin.math.abs

import com.google.maps.android.SphericalUtil
import kotlin.math.ceil

class MapScreenSimpleViewModel(
    private val repository: AddressRepository = AddressRepository(AdressDataSource())
): ViewModel() {

    private val _coordinates = MutableLiveData<Pair<Double, Double>>()
    val coordinates: LiveData<Pair<Double, Double>> = _coordinates

    //could add adress name
    var polygondata= mutableStateListOf<LatLng>()

    fun addPoint(latLng: LatLng) {
        if (polygondata.size < 10) {
            polygondata.add(latLng)

        }
    }
    fun calculateAreaOfPolygon(latLngList: List<LatLng>): Int {
        // Convert LatLng list to a polygon area
        val polygonArea = SphericalUtil.computeArea(latLngList)
        val roundedArea = ceil(polygonArea ).toInt()
        return roundedArea
    }
    fun calculateArea(points:List<LatLng>): Double {
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

    fun removePoints(){
        polygondata.clear()
    }
    fun removeLastPoint(){
        if (polygondata.size > 0){
            polygondata.removeAt(polygondata.size-1)
        }

    }

    fun fetchCoordinates(address: String) {
        viewModelScope.launch {
            try {
                val result = repository.getCoordinates(address)
                if (result.isNotEmpty()) {
                    val coordinate = result[0]
                    _coordinates.postValue(Pair(coordinate.lat.toDouble(), coordinate.lon.toDouble()))

                }

            } catch (e: Exception) {
                // Handle error, like no network, etc.
                Log.e("MyViewModel", "Error fetching coordinates", e)
            }
        }
    }
    // to change if map is clicked not used
    fun selectLocation(lat: Double, lon: Double) {
        _coordinates.postValue(lat to lon)  // 🔹 Updates lat/lon
    }

}

