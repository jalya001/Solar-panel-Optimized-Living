package no.solcellepanelerApp.data.weatherdata

<<<<<<< HEAD
=======
import no.solcellepanelerApp.model.weather.Radiation
import no.solcellepaneller.data.weatherdata.FrostApi
import java.time.ZoneId
import java.time.ZonedDateTime

>>>>>>> origin/price+nergy
class WeatherRepository(
    private val pvgisDataSource: PVGISApi = PVGISApi(),
    private val frostDataSource: FrostApi = FrostApi(),
    private val client: CustomHttpClient = CustomHttpClient(),
) {
    private suspend fun getRadiationData(
        lat: Double,
        long: Double,
        slope: Int,
        azimuth: Int,
    ): Result<Array<Double>> {
        return pvgisDataSource.getRadiation(client, lat, long, slope, azimuth)
    }

    private suspend fun getFrostData(
        lat: Double,
        lon: Double,
<<<<<<< HEAD
        elements: List<String>,
    ): Result<MutableMap<String, Array<Double>>> {
        return frostDataSource.fetchFrostData(client, lat, lon, elements)
    }

    suspend fun getPanelWeatherData(
        lat: Double,
        lon: Double,
        slope: Int,
        azimuth: Int,
    ): Result<Map<String, Array<Double>>> {
        val radiationResult = getRadiationData(lat, lon, slope, azimuth)
        if (radiationResult.isFailure) return Result.failure(radiationResult.exceptionOrNull()!!)
        val radiationData = radiationResult.getOrNull()
        val frostElements = listOf(
            "mean(snow_coverage_type P1M)",
            "mean(air_temperature P1M)",
            "mean(cloud_area_fraction P1M)"
        )
        val frostResult = getFrostData(lat, lon, frostElements)
        if (frostResult.isFailure) return Result.failure(frostResult.exceptionOrNull()!!)

        val dataMap: MutableMap<String, Array<Double>> = frostResult.getOrNull()?: mutableMapOf()
        if (!radiationData.isNullOrEmpty()) {
            dataMap["mean(PVGIS_radiation P1M)"] = radiationData
        }
        return Result.success(dataMap)
=======
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
>>>>>>> origin/price+nergy
    }
}
