package no.solcellepanelerApp.ui.electricity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.solcellepanelerApp.data.electricitydata.ElectricityPriceRepository
import no.solcellepanelerApp.model.electricity.ElectricityPrice
import no.solcellepanelerApp.model.electricity.Region
import java.time.LocalDate

sealed interface PriceUiState {
    data class Success(val prices: List<ElectricityPrice>) : PriceUiState
    data object Error : PriceUiState
    data object Loading : PriceUiState
}

class PriceScreenViewModel(
    private val repository: ElectricityPriceRepository= ElectricityPriceRepository(),

) : ViewModel() {
    private val _priceUiState = MutableStateFlow<PriceUiState>(PriceUiState.Loading)
    val priceUiState: StateFlow<PriceUiState> = _priceUiState.asStateFlow()

    private val _region = MutableStateFlow<Region?>(Region.TROMSO)
    val region: StateFlow<Region?> = _region


    init {
        fetchPrices(LocalDate.now())
    }
    fun setRegion(region: Region) {
        _region.value = region
        repository.updateRegion(region)
        fetchPrices(LocalDate.now())
    }
    private fun fetchPrices(date: LocalDate) {
        viewModelScope.launch {
            _priceUiState.value = PriceUiState.Loading
            val selectedRegion = region.value
            Log.d("PriceScreenViewModel", "Henter strømpriser for region: $selectedRegion")

            try {
                if (selectedRegion != null) {
                    repository.updateRegion(selectedRegion)
                } // optional if already updated in setRegion()
                repository.updatePrices(date)
                val prices = repository.getPrices(date)

                if (prices.isNotEmpty()) {
                    _priceUiState.value = PriceUiState.Success(prices)
                    Log.d("PriceScreenViewModel", "Hentet ${prices.size} priser")
                } else {
                    _priceUiState.value = PriceUiState.Error
                    Log.e("PriceScreenViewModel", "Ingen priser funnet for region: $selectedRegion på $date")
                }
            } catch (e: Exception) {
                _priceUiState.value = PriceUiState.Error
                Log.e("PriceScreenViewModel", "Nettverksfeil ved henting av strømpriser", e)
            }
        }
    }
}