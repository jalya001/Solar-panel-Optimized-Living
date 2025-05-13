package no.SOL.model.price

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import no.SOL.data.pricedata.PriceRepository
import no.SOL.model.reusables.UiState
import java.time.ZonedDateTime

suspend fun fetchPrices(
    priceUiState: MutableStateFlow<UiState>,
    date: ZonedDateTime,
    priceRepository: PriceRepository,
) {
    priceUiState.value = UiState.LOADING
    Log.d(
        "PriceViewModel",
        "Fetching electricity prices for region: ${priceRepository.region.value}"
    )

    try {
        priceRepository.updatePrices(date)
        Log.d(
            "PriceViewModel",
            "Fetched ${priceRepository.prices.value[priceRepository.region.value]!!.data}"
        )

        if (priceRepository.prices.value[priceRepository.region.value]!!.data.isNotEmpty()) {
            priceUiState.value = UiState.SUCCESS
            Log.d(
                "PriceViewModel",
                "Fetched ${priceRepository.prices.value[priceRepository.region.value]!!.data.size} prices"
            )
        } else {
            priceUiState.value = UiState.ERROR
            Log.e(
                "PriceViewModel",
                "No prices found for region: ${priceRepository.region.value} on $date"
            )
        }
    } catch (e: Exception) {
        priceUiState.value = UiState.ERROR
        Log.e("PriceViewModel", "Network error while fetching electricity prices", e)
    }
}
