package no.solcellepanelerApp.data.mapdata

import no.solcellepanelerApp.model.map.GeocodingResponse

class AddressRepository(private val dataSource: AdressDataSource) {

    suspend fun getCoordinates(address: String): List<GeocodingResponse> {
        return dataSource.getCoordinates(address)
    }

}

