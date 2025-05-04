package no.solcellepanelerApp.data.mapdata

import no.solcellepanelerApp.model.map.GeocodingResponse

class AddressRepository(
    private val dataSource: AddressDataSource,
    private val elevationApi: ElevationApi
) {

    suspend fun getCoordinates(address: String): List<GeocodingResponse> {
        return dataSource.getCoordinates(address)
    }

    suspend fun getHeight(coordinates: Pair<Double,Double>): Double? {
        return elevationApi.fetchElevation(coordinates)
    }
}

