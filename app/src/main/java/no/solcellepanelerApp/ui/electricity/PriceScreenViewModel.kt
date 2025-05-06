package no.solcellepanelerApp.ui.electricity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import no.solcellepanelerApp.data.electricitydata.ElectricityPriceRepository
import no.solcellepanelerApp.model.electricity.ElectricityPrice
import no.solcellepanelerApp.model.electricity.Region
import java.time.LocalDate

class PriceScreenViewModel(
    private val repository: ElectricityPriceRepository = ElectricityPriceRepository(),

) : ViewModel() {
    enum class UiState {
        LOADING, SUCCESS, ERROR
    }

    private val _priceUiState = MutableStateFlow(UiState.LOADING)
    val priceUiState: StateFlow<UiState> = _priceUiState

    private val _prices = MutableStateFlow<List<ElectricityPrice>?>(null)
    val prices: StateFlow<List<ElectricityPrice>?> = _prices

    private val _region = MutableStateFlow<Region?>(Region.TROMSO)
    val region: StateFlow<Region?> = _region

    init {
        fetchPrices(LocalDate.now()) // Date should be centralized
    }
    fun setRegion(region: Region) {
        _region.value = region
        repository.updateRegion(region)
        fetchPrices(LocalDate.now()) // Date should be centralized
    }
    private fun fetchPrices(date: LocalDate) {
        viewModelScope.launch {
            _priceUiState.value = UiState.LOADING
            val selectedRegion = region.value
            Log.d("PriceScreenViewModel", "Henter strømpriser for region: $selectedRegion")

            try {
                if (selectedRegion != null) {
                    repository.updateRegion(selectedRegion)
                } // optional if already updated in setRegion()
                repository.updatePrices(date)
                _prices.value = repository.getPrices(date)

                if (_prices.value?.isNotEmpty() == true) {
                    _priceUiState.value = UiState.SUCCESS
                    Log.d("PriceScreenViewModel", "Hentet ${_prices.value!!.size} priser")
                } else {
                    _priceUiState.value = UiState.ERROR
                    Log.e("PriceScreenViewModel", "Ingen priser funnet for region: $selectedRegion på $date")
                }
            } catch (e: Exception) {
                _priceUiState.value = UiState.ERROR
                Log.e("PriceScreenViewModel", "Nettverksfeil ved henting av strømpriser", e)
            }
        }
    }
}