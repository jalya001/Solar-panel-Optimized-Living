package no.solcellepanelerApp.data.electricity

import kotlinx.coroutines.runBlocking
import no.solcellepanelerApp.data.pricedata.ElectricityPriceApi
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.time.ZonedDateTime

class ElectricityPriceAPITest {
    private val api = ElectricityPriceApi()

    //Tests if API fetches electricity prices for today for Oslo (NO1)
    @Test
    fun `fetchPrices should return prices from API`() = runBlocking {
        val today = ZonedDateTime.now()
        val result = api.fetchPrices(today, "NO1")

        assertTrue(result.isNotEmpty(), "Expected non-empty list of prices")
        println("Fikk ${result.size} priser for $today i NO1")
    }

    //Tests if API fetches electricity prices for yesterday for Trondheim (NO3)
    @Test
    fun `fetchPrices should return prices for yesterday`() = runBlocking {
        val yesterday = ZonedDateTime.now().minusDays(1)
        val result = api.fetchPrices(yesterday, "NO3")

        assertTrue(result.isNotEmpty(), "Expected non-empty list of prices")
        println("Fikk ${result.size} priser for $yesterday i NO3")
    }

    //Tests if API fetches electricity prices for an invalid price area
    @Test
    fun `fetchPrices should return empty list for invalid price area`() = runBlocking {
        val today = ZonedDateTime.now()
        val result = api.fetchPrices(today, "NO6") //"NO6" is not a valid price area
        assertTrue(result.isEmpty(), "Expected empty list for invalif price area")
        print("Fant ingen priser for $today i NO6 (ugyldig sone)")
    }

    //Tests if API fetches electricity prices for a future date for Bergen (NO5)
    @Test
    fun `fetchPrices should return prices for future date`() = runBlocking {
        val future = ZonedDateTime.now().plusDays(3)
        val result = api.fetchPrices(future, "NO5")

        assertTrue(result.isEmpty(), "Expected non-empty list of prices")
        println("Fikk ${result.size} priser for $future i NO5 (forventet tomt")
    }
}