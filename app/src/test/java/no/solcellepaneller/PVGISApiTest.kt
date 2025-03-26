package no.solcellepaneller

import kotlinx.coroutines.runBlocking
import no.solcellepaneller.data.weatherdata.PVGISApi
import org.junit.Assert.*
import org.junit.Test

class PVGISApiTest {

    @Test
    fun testGetSolarEnergy() = runBlocking {
        val dataSource = PVGISApi()
        val energyData = dataSource.getSolarEnergy()

        assertTrue("Should return energy data", energyData.isNotEmpty())
        println("Fetched energy data: $energyData")
    }
}
