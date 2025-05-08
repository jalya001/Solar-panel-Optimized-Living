package no.solcellepanelerApp.data.mapdata

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.launch
import no.solcellepanelerApp.data.weatherdata.WeatherRepository
import no.solcellepanelerApp.model.map.GeocodingResponse
import kotlin.math.ceil

class UserDataRepository(
    private val dataSource: AddressDataSource,
    private val elevationApi: ElevationApi
) {
/*

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
*/
    suspend fun getCoordinates(address: String): List<GeocodingResponse> {
        return dataSource.getCoordinates(address)
    }

    suspend fun getHeight(coordinates: Pair<Double,Double>): Double? {
        return elevationApi.fetchElevation(coordinates)
    }

    object UserDataRepositoryProvider {
        val instance: UserDataRepository by lazy {
            UserDataRepository(AddressDataSource(), ElevationApi())
        }
    }
}

