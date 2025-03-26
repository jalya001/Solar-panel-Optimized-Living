package no.solcellepaneller.data.homedata

import no.solcellepaneller.model.electricity.ElectricityPrice
import java.time.LocalDate

class ElectricityPriceRepository(private val priceArea: String) {
    private val api = ElectricityPriceApi()
    private var prices: List<ElectricityPrice> = emptyList()

    suspend fun updatePrices(date: LocalDate, region: String) {
        prices = api.fetchPrices(date, region)
    }

    suspend fun getPrices(date: LocalDate, region: String): List<ElectricityPrice> {
        val rawPrices = api.fetchPrices(date, region)
        return rawPrices.map { price ->
            price.copy(
                region = region,
                date = date.toString()
            )
        }
    }
}