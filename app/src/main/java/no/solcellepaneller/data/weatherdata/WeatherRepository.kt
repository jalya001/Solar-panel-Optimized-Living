package no.solcellepaneller.data.weatherdata

import no.solcellepaneller.model.weather.Radiation

class WeatherRepository(private val dataSource: PVGISApi){
    suspend fun getRadiationInfo(lat: Double, long: Double, slope: Int): List<Radiation>{
        return dataSource.getRadiation(lat, long, slope)
    }
}
