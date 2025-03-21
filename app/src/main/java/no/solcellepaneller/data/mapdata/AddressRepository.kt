package no.solcellepaneller.data.mapdata

import no.solcellepaneller.model.map.AdressData
import android.location.Geocoder
import no.solcellepaneller.model.map.GeocodingResponse

class AddressRepository(private val dataSource: AdressDataSource){

    suspend fun getCoordinates(address: String): List<GeocodingResponse> {
        return dataSource.getCoordinates(address)
    }

}

