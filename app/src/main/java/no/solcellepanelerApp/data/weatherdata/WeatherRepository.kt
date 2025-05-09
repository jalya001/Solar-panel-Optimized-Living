package no.solcellepanelerApp.data.weatherdata

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import no.solcellepanelerApp.model.reusables.TimedData

class WeatherRepository(
    private val pvgisDataSource: PVGISApi = PVGISApi(),
    private val frostDataSource: FrostApi = FrostApi(),
    private val client: CustomHttpClient = CustomHttpClient(),
) {
    private val _rimData = MutableStateFlow<TimedData<Array<Double>>?>(null)
    val rimData: StateFlow<TimedData<Array<Double>>?> = _rimData

    private val _weatherData = MutableStateFlow<Map<String, Array<Double>>?>(null)
    val weatherData: StateFlow<Map<String, Array<Double>>?> = _weatherData

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
        _weatherData.value = dataMap
        return Result.success(dataMap)
    }

    suspend fun fetchRimData(
        lat: Double,
        lon: Double,
        elements: String
    ) {
        val result = frostDataSource.fetchRimData(client, lat, lon, elements)
        result.onSuccess { body ->
            _rimData.value = TimedData(body)
        }.onFailure {
            _rimData.value = TimedData(emptyArray())
        }
    }

    object WeatherRepositoryProvider {
        val instance: WeatherRepository by lazy {
            WeatherRepository()
        }
    }
}