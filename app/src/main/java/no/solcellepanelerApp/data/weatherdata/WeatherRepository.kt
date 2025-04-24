package no.solcellepanelerApp.data.weatherdata

import no.solcellepanelerApp.model.weather.Radiation
import no.solcellepaneller.data.weatherdata.FrostApi
import java.time.ZoneId
import java.time.ZonedDateTime

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
        elements: List<String>
    ): Map<String, Array<Double>> {
        val exampleTimeRange: Pair<ZonedDateTime, ZonedDateTime> = Pair(
            ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
            ZonedDateTime.now(ZoneId.of("UTC"))
        )
        val result = frostDataSource.fetchFrostData(lat, lon, elements, exampleTimeRange)
        result.onSuccess { body ->
            return body
        }.onFailure { error ->
            return emptyMap() // TBD: Implement actual error-handling
        }
        return emptyMap()
    }
    suspend fun getRimData(
        lat: Double,
        lon: Double,
        elements: String
    ): Array<Double> {


        val result = frostDataSource.fetchRimData(lat, lon, elements)
        result.onSuccess { body ->
            return body
        }.onFailure { error ->
            return emptyArray() // TBD: Implement actual error-handling
        }
        return emptyArray()
    }
}
