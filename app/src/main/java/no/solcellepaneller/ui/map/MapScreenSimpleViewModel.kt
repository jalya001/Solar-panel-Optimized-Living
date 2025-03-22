package no.solcellepaneller.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.solcellepaneller.data.mapdata.AddressRepository
import no.solcellepaneller.data.mapdata.AdressDataSource

class MapScreenSimpleViewModel(
    private val repository: AddressRepository = AddressRepository(AdressDataSource())
) : ViewModel() {

    private val _coordinates = MutableLiveData<Pair<Double, Double>>()
    val coordinates: LiveData<Pair<Double, Double>> = _coordinates

    fun fetchCoordinates(address: String) {
        viewModelScope.launch {
            try {
                val result = repository.getCoordinates(address)
                if (result.isNotEmpty()) {
                    val coordinate = result[0]
                    _coordinates.postValue(Pair(coordinate.lat.toDouble(), coordinate.lon.toDouble()))
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error fetching coordinates", e)
            }
        }
    }

    fun selectLocation(lat: Double, lon: Double) {
        _coordinates.postValue(lat to lon)
    }
}
