package no.solcellepanelerApp.data.electricitydata

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import no.solcellepanelerApp.model.electricity.ElectricityPrice
import no.solcellepanelerApp.model.electricity.Region
import no.solcellepanelerApp.model.reusables.TimedData
import java.time.LocalDate

class ElectricityPriceRepository {
    private val api = ElectricityPriceApi()
    private var selectedRegion: String = Region.OSLO.regionCode

    private val _prices = MutableStateFlow<TimedData<List<ElectricityPrice>>?>(null)
    val prices: StateFlow<TimedData<List<ElectricityPrice>>?> = _prices

    fun updateRegion(region: Region) {
        selectedRegion = region.regionCode
    }

    suspend fun updatePrices(date: LocalDate) {
        _prices.value = TimedData(api.fetchPrices(date, selectedRegion))
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
