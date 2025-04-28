package no.solcellepanelerApp.data.weatherdata

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

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
        coEvery { mockPvgisApi.getRadiation(any(), any(), any(), any(), any()) } returns Result.success(testArray)
        coEvery { mockFrostApi.fetchFrostData(any(), any(), any(), any(), any()) } returns Result.success(testMap)

        val result = weatherRepository.getPanelWeatherData(10.0, 20.0, 30, 40)
        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertEquals(4, data?.size)
        assertTrue(data?.containsKey("mean(PVGIS_radiation P1M)") == true)
    }

    @Test
    fun `test failure when radiation data fails`() = runBlocking {
        coEvery { mockPvgisApi.getRadiation(any(), any(), any(), any(), any()) } returns Result.failure(ApiException(ApiError.UNKNOWN_ERROR))
        coEvery { mockFrostApi.fetchFrostData(any(), any(), any(), any(), any()) } returns Result.success(testMap)

        val result = weatherRepository.getPanelWeatherData(10.0, 20.0, 30, 40)
        assertTrue(result.isFailure)
    }

    @Test
    fun `test failure when frost data fails`() = runBlocking {
        coEvery { mockPvgisApi.getRadiation(any(), any(), any(), any(), any()) } returns Result.success(testArray)
        coEvery { mockFrostApi.fetchFrostData(any(), any(), any(), any(), any()) } returns Result.failure(ApiException(ApiError.UNKNOWN_ERROR))

        val result = weatherRepository.getPanelWeatherData(10.0, 20.0, 30, 40)
        assertTrue(result.isFailure)
    }

    @Test
    fun `test success when frost is empty`() = runBlocking {
        coEvery { mockPvgisApi.getRadiation(any(), any(), any(), any(), any()) } returns Result.success(testArray)
        coEvery { mockFrostApi.fetchFrostData(any(), any(), any(), any(), any()) } returns Result.success(mutableMapOf())

        val result = weatherRepository.getPanelWeatherData(10.0, 20.0, 30, 40)
        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertEquals(1, data?.size)
        assertTrue(data?.containsKey("mean(PVGIS_radiation P1M)") == true)
    }

    @Test
    fun `test success when pvgis is empty`() = runBlocking {
        coEvery { mockPvgisApi.getRadiation(any(), any(), any(), any(), any()) } returns Result.success(arrayOf())
        coEvery { mockFrostApi.fetchFrostData(any(), any(), any(), any(), any()) } returns Result.success(testMap)

        val result = weatherRepository.getPanelWeatherData(10.0, 20.0, 30, 40)
        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertEquals(3, data?.size)
        assertTrue(data?.containsKey("mean(PVGIS_radiation P1M)") == false)
        assertTrue(data?.containsKey("mean(air_temperature P1M)") == true)
    }
}
