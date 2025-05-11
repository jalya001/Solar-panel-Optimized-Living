package no.solcellepanelerApp.data.userdata

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import no.solcellepanelerApp.model.electricity.Region
import no.solcellepanelerApp.model.map.GeocodingResponse
import no.solcellepanelerApp.ui.reusables.StateFlowDelegate

class UserDataRepository(
    private val addressApi: AddressApi,
    private val elevationApi: ElevationApi
) {
    var areaState = StateFlowDelegate(0.0)
    var angleState = StateFlowDelegate(0.0)
    var directionState = StateFlowDelegate(0.0)
    var efficiencyState = StateFlowDelegate(0.0)
    var selectedRegionState = StateFlowDelegate(Region.OSLO)
    var coordinatesState = StateFlowDelegate<LatLng?>(null)

    private val _height = MutableStateFlow<Double?>(null)
    val height: StateFlow<Double?> = _height

    suspend fun getCoordinates(address: String): List<GeocodingResponse> {
        val result = addressApi.getCoordinates(address)
        if (result.isNotEmpty()) {
            val coords = result.first()
            val coordinatePair = LatLng(coords.lat.toDouble(), coords.lon.toDouble())
            coordinatesState.value = coordinatePair
        }
        return result
    }

    suspend fun fetchHeight() {
        if (coordinatesState.value != null) {
            _height.value = elevationApi.fetchElevation(coordinatesState.value!!)
        } else {
            _height.value = null
        }
    }

    object UserDataRepositoryProvider {
        val instance: UserDataRepository by lazy {
            UserDataRepository(AddressApi(), ElevationApi())
        }
    }
}

