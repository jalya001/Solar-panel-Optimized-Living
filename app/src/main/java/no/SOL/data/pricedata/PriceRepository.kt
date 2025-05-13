package no.SOL.data.pricedata

import android.util.Log
import no.SOL.model.price.ElectricityPrice
import no.SOL.model.price.Region
import no.SOL.model.reusables.TimedData
import no.SOL.model.reusables.updateStaleData
import no.SOL.ui.reusables.StateFlowDelegate
import java.time.ZonedDateTime

class PriceRepository {
    private val api = ElectricityPriceApi()

    var region = StateFlowDelegate(Region.OSLO)
    var prices = StateFlowDelegate<MutableMap<Region, TimedData<List<ElectricityPrice>>?>>(
        Region.entries.associateWith { null }.toMutableMap()
    )
    // Maybe we shouldn't expose the time and mutability?

    suspend fun updatePrices(date: ZonedDateTime) {
        updateStaleData(
            currentTime = date,
            getData = { prices.value[region.value] },
            setData = { newData -> prices.value[region.value] = newData },
            fetchData = {
                api.fetchPrices(date, region.value.regionCode).takeIf { it.isNotEmpty() }
            }
        )
        Log.d("PriceRepository", "Prices: ${prices.value[region.value]?.data}")
    }

    fun getCurrentRegionsAverage(): Double? {
        val currentPrices = prices.value[region.value]?.data
        return currentPrices?.takeIf { it.isNotEmpty() }?.map { it.NOK_per_kWh }?.average()
    }

    /*
        suspend fun getPrices(date: ZonedDateTime, region: Region): List<ElectricityPrice> {
            val rawPrices = api.fetchPrices(date, region.regionCode)
            return rawPrices.map { price ->
                price.copy(
                    region = region.regionCode,
                    date = date.toString()
                )
            }
        }
    */
    object PriceRepositoryProvider {
        val instance: PriceRepository by lazy {
            PriceRepository()
        }
    }
}
