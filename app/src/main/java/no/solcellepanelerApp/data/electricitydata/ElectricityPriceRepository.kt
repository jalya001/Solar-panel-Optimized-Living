package no.solcellepanelerApp.data.electricitydata

import no.solcellepanelerApp.model.electricity.ElectricityPrice
import no.solcellepanelerApp.model.electricity.Region
import java.time.LocalDate

class ElectricityPriceRepository {
    private val api = ElectricityPriceApi()
    private var prices: List<ElectricityPrice> = emptyList()
    private var selectedRegion: String = Region.TROMSO.regionCode

    fun updateRegion(region: Region) {
        selectedRegion = region.regionCode
    }

    suspend fun updatePrices(date: LocalDate) {
        prices = api.fetchPrices(date, selectedRegion)
    }

    suspend fun getPrices(date: LocalDate): List<ElectricityPrice> {
        val rawPrices = api.fetchPrices(date, selectedRegion)
        return rawPrices.map { price ->
            price.copy(
                region = selectedRegion,
                date = date.toString()
            )
        }
    }

    object ElectricityPriceRepositoryProvider {
        val instance: ElectricityPriceRepository by lazy {
            ElectricityPriceRepository()
        }
    }
}
