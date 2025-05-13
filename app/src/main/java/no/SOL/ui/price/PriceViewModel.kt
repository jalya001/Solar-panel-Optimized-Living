package no.SOL.ui.price

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import no.SOL.data.pricedata.PriceRepository
import no.SOL.model.price.Region
import no.SOL.model.price.fetchPrices
import no.SOL.model.reusables.UiState
import java.time.ZonedDateTime

class PriceViewModel : ViewModel() {
    private val priceRepository = PriceRepository.PriceRepositoryProvider.instance

    val region = priceRepository.region
    val prices = priceRepository.prices

    private val _priceUiState = MutableStateFlow(UiState.LOADING)
    val priceUiState: StateFlow<UiState> = _priceUiState

    private val date = ZonedDateTime.now()

    fun setRegion(newRegion: Region) {
        region.value = newRegion

        viewModelScope.launch {
            doFetchPrices()
        }
    }

    suspend fun doFetchPrices() {
        fetchPrices(_priceUiState, date, priceRepository)
    }
}