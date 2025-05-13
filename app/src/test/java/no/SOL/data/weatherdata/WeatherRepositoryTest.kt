package no.SOL.data.weatherdata

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.assertThrows

class WeatherRepositoryTest {
    private val mockPvgisApi = mockk<PVGISApi>()
    private val mockFrostApi = mockk<FrostApi>()
    private val weatherRepository = WeatherRepository(
        pvgisDataSource = mockPvgisApi,
        frostDataSource = mockFrostApi
    )

    private val testArray = Array(12) { i -> (i + 1).toDouble() }
    private val testMap = mutableMapOf(
        "mean(snow_coverage_type P1M)" to testArray,
        "mean(air_temperature P1M)" to testArray,
        "mean(cloud_area_fraction P1M)" to testArray
    )

    @Test
    fun `test successful weather data retrieval`() = runBlocking {
        coEvery {
            mockPvgisApi.getRadiation(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Result.success(testArray)
        coEvery {
            mockFrostApi.fetchFrostData(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Result.success(testMap)

        val weatherDataFlow = weatherRepository.weatherData
        weatherRepository.getPanelWeatherData(10.0, 20.0, 30.0, 30, 40)
        assertEquals(4, weatherDataFlow.value?.size)
        assertTrue(weatherDataFlow.value?.containsKey("mean(PVGIS_radiation P1M)") == true)
    }

    @Test
    fun `test failure when radiation data fails`(): Unit = runBlocking {
        coEvery {
            mockPvgisApi.getRadiation(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Result.failure(ApiException(ApiError.UNKNOWN_ERROR))
        coEvery {
            mockFrostApi.fetchFrostData(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Result.success(testMap)

        assertThrows<Throwable> {
            weatherRepository.getPanelWeatherData(10.0, 20.0, 30.0, 30, 40)
        }
    }

    @Test
    fun `test failure when frost data fails`(): Unit = runBlocking {
        coEvery {
            mockPvgisApi.getRadiation(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Result.success(testArray)
        coEvery {
            mockFrostApi.fetchFrostData(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Result.failure(ApiException(ApiError.UNKNOWN_ERROR))

        assertThrows<Throwable> {
            weatherRepository.getPanelWeatherData(10.0, 20.0, 30.0, 30, 40)
        }
    }

    @Test
    fun `test success when frost is empty`() = runBlocking {
        coEvery {
            mockPvgisApi.getRadiation(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Result.success(testArray)
        coEvery {
            mockFrostApi.fetchFrostData(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Result.success(mutableMapOf())

        val weatherDataFlow = weatherRepository.weatherData
        weatherRepository.getPanelWeatherData(10.0, 20.0, 30.0, 30, 40)
        assertEquals(1, weatherDataFlow.value?.size)
        assertTrue(weatherDataFlow.value?.containsKey("mean(PVGIS_radiation P1M)") == true)
    }

    @Test
    fun `test success when pvgis is empty`() = runBlocking {
        coEvery {
            mockPvgisApi.getRadiation(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Result.success(arrayOf())
        coEvery {
            mockFrostApi.fetchFrostData(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Result.success(testMap)

        val weatherDataFlow = weatherRepository.weatherData
        weatherRepository.getPanelWeatherData(10.0, 20.0, 30.0, 30, 40)
        assertEquals(3, weatherDataFlow.value?.size)
        assertTrue(weatherDataFlow.value?.containsKey("mean(PVGIS_radiation P1M)") == false)
        assertTrue(weatherDataFlow.value?.containsKey("mean(air_temperature P1M)") == true)
    }
}
