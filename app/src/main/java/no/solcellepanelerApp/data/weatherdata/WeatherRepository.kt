package no.solcellepanelerApp.data.weatherdata

import no.solcellepanelerApp.model.weather.Radiation

class WeatherRepository(
    private val pvgisDataSource: PVGISApi = PVGISApi(),
    private val frostDataSource: FrostApi = FrostApi(),
) {
    suspend fun getRadiationInfo(
        lat: Double,
        long: Double,
        slope: Int,
        azimuth: Int,
    ): List<Radiation> {
        return pvgisDataSource.getRadiation(lat, long, slope, azimuth)
    }

    suspend fun getFrostData(
        lat: Double,
        lon: Double,
        elements: List<String>,
    ): Map<String, Array<Double>> {
        return frostDataSource.fetchFrostData(lat, lon, elements)
    }
}
