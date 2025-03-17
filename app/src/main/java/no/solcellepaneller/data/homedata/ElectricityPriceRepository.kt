package no.solcellepaneller.data.homedata

import no.solcellepaneller.model.electricity.ElectricityPrice
import java.time.LocalDate

class ElectricityPriceRepository(private val priceArea: String) {
    private val api = ElectricityPriceApi()
    private var prices: List<ElectricityPrice> = emptyList()

    suspend fun updatePrices(date: LocalDate) {
        prices = api.fetchPrices(date, priceArea)
    }

    fun getPrices(): List<ElectricityPrice> {
        return prices
    }
}