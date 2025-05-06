package no.solcellepanelerApp.data.weatherdata

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
        height: Double?,
        elements: List<String>,
    ): Result<MutableMap<String, Array<Double>>> {
        return frostDataSource.fetchFrostData(client, lat, lon, height, elements)
    }

    suspend fun getPanelWeatherData(
        lat: Double,
        lon: Double,
        height: Double?,
        slope: Int,
        azimuth: Int,
    ): Result<Map<String, Array<Double>>> {
        val radiationResult = getRadiationData(lat, lon, slope, azimuth)
        if (radiationResult.isFailure) return Result.failure(radiationResult.exceptionOrNull()!!)
        val radiationData = radiationResult.getOrNull()
        val frostElements = listOf(
            "mean(air_temperature P1M)", // This needs to come before snow, or else it will give an error
            "mean(snow_coverage_type P1M)",
            "mean(cloud_area_fraction P1M)"
        )
        val frostResult = getFrostData(lat, lon, height, frostElements)
        if (frostResult.isFailure) return Result.failure(frostResult.exceptionOrNull()!!)

        val dataMap: MutableMap<String, Array<Double>> = frostResult.getOrNull()?: mutableMapOf()
        if (!radiationData.isNullOrEmpty()) {
            dataMap["mean(PVGIS_radiation P1M)"] = radiationData
        }
        return Result.success(dataMap)
    }

    suspend fun getRimData(
        lat: Double,
        lon: Double,
        elements: String
    ): Array<Double> {


        val result = frostDataSource.fetchRimData(client, lat, lon, elements)
        result.onSuccess { body ->
            return body
        }.onFailure { error ->
            return emptyArray() // TBD: Implement actual error-handling
        }
        return emptyArray()
    }

    object WeatherRepositoryProvider {
        val instance: WeatherRepository by lazy {
            WeatherRepository()
        }
    }
}