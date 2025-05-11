package no.solcellepanelerApp.data.electricitydata

import no.solcellepanelerApp.model.electricity.ElectricityPrice
import no.solcellepanelerApp.model.electricity.Region
import java.time.LocalDate

// Repository class responsible for managing and providing electricity price data
class ElectricityPriceRepository {
    private val api = ElectricityPriceApi()
    private var prices: List<ElectricityPrice> = emptyList()
    private var selectedRegion: String = Region.OSLO.regionCode

    // Updates the currently selected region for fetching prices
    fun updateRegion(region: Region) {
        selectedRegion = region.regionCode
    }

    // Fetches and caches electricity prices from API for selected region and date
    suspend fun updatePrices(date: LocalDate) {
        prices = api.fetchPrices(date, selectedRegion)
    }

    // Returns electricity prices for the selected region and date, updating region and date fields
    suspend fun getPrices(date: LocalDate): List<ElectricityPrice> {
        val rawPrices = api.fetchPrices(date, selectedRegion)
        return rawPrices.map { price ->
            price.copy(
                region = selectedRegion,
                date = date.toString()
            )
        }
    }
}
