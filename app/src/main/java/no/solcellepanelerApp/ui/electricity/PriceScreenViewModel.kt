package no.solcellepanelerApp.ui.electricity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.solcellepanelerApp.data.homedata.ElectricityPriceRepository
import no.solcellepanelerApp.model.electricity.ElectricityPrice
import java.time.LocalDate

sealed interface PriceUiState {
    data class Success(val prices: List<ElectricityPrice>) : PriceUiState
    data object Error : PriceUiState
    data object Loading : PriceUiState
}

class PriceScreenViewModel(
    private val repository: ElectricityPriceRepository,
    private val region: String,
) : ViewModel() {
    private val _priceUiState = MutableStateFlow<PriceUiState>(PriceUiState.Loading)
    val priceUiState: StateFlow<PriceUiState> = _priceUiState.asStateFlow()

    init {
        fetchPrices(LocalDate.now())
    }

    private fun fetchPrices(date: LocalDate) {
        viewModelScope.launch {
            _priceUiState.value = PriceUiState.Loading
            Log.d("PriceScreenViewModel", "Henter strømpriser for region: $region")
            try {
                repository.updatePrices(date, region)
                val prices = repository.getPrices(date, region)
                if (prices.isNotEmpty()) {
                    _priceUiState.value = PriceUiState.Success(prices)
                    Log.d("PriceScreenViewModel", "Hentet ${prices.size} priser")
                } else {
                    _priceUiState.value = PriceUiState.Error
                    Log.e(
                        "PriceScreenViewModel",
                        "Feil: Ingen priser funnet for region: $region på $date"
                    )
                }
            } catch (e: Exception) {
                _priceUiState.value = PriceUiState.Error
                Log.e("PriceScreenViewModel", "Nettverksfeil ved henting av strømpriser", e)
            }
        }
    }
}