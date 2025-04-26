package no.solcellepanelerApp

import no.solcellepanelerApp.util.mapLocationToRegion
import org.junit.Assert.assertEquals
import org.junit.Test
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import no.solcellepanelerApp.model.electricity.Region
import no.solcellepanelerApp.ui.map.MapScreenViewModel
import no.solcellepanelerApp.ui.result.calculateMonthlyEnergyOutput
import org.junit.Assert.assertEquals
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}



class DeviceConnectionTest {

    @Test
    fun `adding device should decrease energy and add to connected devices`() {
        // Given
        val initialEnergy = 100.0
        val deviceName = "TV"
        val deviceConsumption = 15.0

        // When
        val result = toggleDeviceConnection(
            initialEnergy = initialEnergy,
            connectedDevices = mutableMapOf(),
            deviceName = deviceName,
            deviceValue = deviceConsumption
        )

        // Then
        assertEquals(85.0, result.updatedEnergy)
        assertEquals(mapOf(deviceName to deviceConsumption), result.updatedDevices)
    }

    @Test
    fun `removing device should increase energy and remove from connected devices`() {
        // Given
        val initialEnergy = 85.0
        val deviceName = "TV"
        val deviceConsumption = 15.0
        val initialDevices = mutableMapOf(deviceName to deviceConsumption)

        // When
        val result = toggleDeviceConnection(
            initialEnergy = initialEnergy,
            connectedDevices = initialDevices,
            deviceName = deviceName,
            deviceValue = deviceConsumption
        )

        // Then
        assertEquals(100.0, result.updatedEnergy)
        assertEquals(emptyMap<String, Double>(), result.updatedDevices)
    }

    private fun toggleDeviceConnection(
        initialEnergy: Double,
        connectedDevices: MutableMap<String, Double>,
        deviceName: String,
        deviceValue: Double
    ): ConnectionResult {
        return if (connectedDevices.containsKey(deviceName)) {
            connectedDevices.remove(deviceName)
            ConnectionResult(initialEnergy + deviceValue, connectedDevices.toMap())
        } else {
            connectedDevices[deviceName] = deviceValue
            ConnectionResult(initialEnergy - deviceValue, connectedDevices.toMap())
        }
    }

    private data class ConnectionResult(
        val updatedEnergy: Double,
        val updatedDevices: Map<String, Double>
    )
}



class EnergyDisplayTest {

    @Test
    fun `positive energy should display normally`() {
        // Given
        val energy = 50.0

        // When
        val displayText = getEnergyDisplayText(energy)

        // Then
        assertEquals("50.00 kWh", displayText)
    }

    @Test
    fun `negative energy should indicate deficit`() {
        // Given
        val energy = -10.0

        // When
        val displayText = getEnergyDisplayText(energy)

        // Then
        assertEquals("Energy deficit! -10.00 kWh", displayText)
    }

    private fun getEnergyDisplayText(energy: Double): String {
        return if (energy < 0) "Energy deficit! %.2f kWh".format(energy)
        else "%.2f kWh".format(energy)
    }
}


class EnergyCalculationTest {

    @Test
    fun `calculateMonthlyEnergyOutput should return correct values`() {
        // Given
        val avgTemp = arrayOf(10.0, 20.0, 30.0)
        val cloudCover = arrayOf(2.0, 4.0, 6.0)
        val snowCover = arrayOf(0.0, 1.0, 2.0)
        val panelArea = 10.0
        val efficiency = 20.0
        val tempCoeff = -0.44
        val radiation = listOf(100.0, 200.0, 300.0)

        // When
        val result = calculateMonthlyEnergyOutput(
            avgTemp, cloudCover, snowCover,
            panelArea, efficiency, tempCoeff, radiation
        )

        // Then
        val expected = listOf(
            100.0 * (1 - 2.0/8) * (1 - 0.0/4) * 10 * 0.2 * (1 + (-0.44) * (10 - 25)),
            200.0 * (1 - 4.0/8) * (1 - 1.0/4) * 10 * 0.2 * (1 + (-0.44) * (20 - 25)),
            300.0 * (1 - 6.0/8) * (1 - 2.0/4) * 10 * 0.2 * (1 + (-0.44) * (30 - 25))
        )

        assertEquals(expected, result)
    }

    @Test
    fun `calculateMonthlyEnergyOutput should handle empty inputs`() {
        // Given
        val emptyArray = emptyArray<Double>()
        val emptyList = emptyList<Double>()

        // When
        val result = calculateMonthlyEnergyOutput(
            emptyArray, emptyArray, emptyArray,
            10.0, 20.0, -0.44, emptyList
        )

        // Then
        assertEquals(emptyList<Double>(), result)
    }
}



class SunAnimationTest {

    @Test
    fun `should select correct animation for energy values`() {
        // Test cases: (input, expected animation)
        val testCases = listOf(
            30.0 to "solar_verylow.json",
            100.0 to "solar_low.json",
            1000.0 to "solar_half.json",
            4000.0 to "solar_full.json"
        )

        testCases.forEach { (value, expected) ->
            val actual = when {
                value < 50.0 -> "solar_verylow.json"
                value in 50.0..500.0 -> "solar_low.json"
                value in 500.0..3000.0 -> "solar_half.json"
                value > 3000.0 -> "solar_full.json"
                else -> "solar_verylow.json"
            }
            assertEquals(expected, actual)
        }
    }
}
class MapScreenViewModelTest {

    private lateinit var viewModel: MapScreenViewModel

    @Before
    fun setup() {
        viewModel = MapScreenViewModel()
    }

    @Test
    fun `addPoint adds a point to polygondata`() {
        // Given empty polygon data
        assertEquals(0, viewModel.polygondata.size)

        // When adding a point
        val point = LatLng(59.9, 10.7)
        viewModel.addPoint(point)

        // Then the point should be added
        assertEquals(1, viewModel.polygondata.size)
        assertEquals(point, viewModel.polygondata[0])
    }

    @Test
    fun `addPoint does not add more than 10 points`() {
        // Given 10 points already added
        repeat(10) { i ->
            viewModel.addPoint(LatLng(59.0 + i, 10.0 + i))
        }

        // When trying to add one more
        viewModel.addPoint(LatLng(70.0, 20.0))

        // Then list should still have 10 points
        assertEquals(10, viewModel.polygondata.size)
    }
}