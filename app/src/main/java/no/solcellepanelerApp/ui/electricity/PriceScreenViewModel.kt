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

//UI state for electricity prices
sealed interface PriceUiState {
    data class Success(val prices: List<ElectricityPrice>) : PriceUiState
    data object Error : PriceUiState
    data object Loading : PriceUiState
}

// Viewmodel that handles logic for fetching and exposing electricity prices
class PriceScreenViewModel(
    private val repository: ElectricityPriceRepository= ElectricityPriceRepository(),

) : ViewModel() {
    private val _priceUiState = MutableStateFlow<PriceUiState>(PriceUiState.Loading)
    val priceUiState: StateFlow<PriceUiState> = _priceUiState.asStateFlow()

    private val _region = MutableStateFlow<Region?>(Region.OSLO)
    val region: StateFlow<Region?> = _region

    // Fetch prices for today when ViewModel is created
    init {
        fetchPrices(LocalDate.now())
    }
    // Updates region and fetches new prices
    fun setRegion(region: Region) {
        _region.value = region
        repository.updateRegion(region)
        fetchPrices(LocalDate.now())
    }
    // Fetches prices for the given date and updates UI state accordingly
    private fun fetchPrices(date: LocalDate) {
        viewModelScope.launch {
            _priceUiState.value = PriceUiState.Loading
            val selectedRegion = region.value
            Log.d("PriceScreenViewModel", "Fetching electricity prices for region: $selectedRegion")

            try {
                if (selectedRegion != null) {
                    repository.updateRegion(selectedRegion) // Redundant if already updated in setRegion()
                }
                repository.updatePrices(date)
                val prices = repository.getPrices(date)

                if (prices.isNotEmpty()) {
                    _priceUiState.value = PriceUiState.Success(prices)
                    Log.d("PriceScreenViewModel", "Fetched ${prices.size} prices")
                } else {
                    _priceUiState.value = PriceUiState.Error
                    Log.e("PriceScreenViewModel", "No electricity prices found for region: $selectedRegion on $date")
                }
            } catch (e: Exception) {
                _priceUiState.value = PriceUiState.Error
                Log.e("PriceScreenViewModel", "Network error while fetching electricity prices", e)
            }
        }
    }
}