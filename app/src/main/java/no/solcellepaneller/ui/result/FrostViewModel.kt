package no.solcellepaneller.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import no.solcellepaneller.data.weatherdata.FrostRepository

class FrostViewModel(
    private val repository: FrostRepository = FrostRepository()
) : ViewModel() {

    private val _frostData = MutableStateFlow<Map<String, Array<Double>>>(emptyMap())
    val frostData: StateFlow<Map<String, Array<Double>>> = _frostData

    fun loadFrostData(lat: Double, lon: Double, elements: List<String>) {
        viewModelScope.launch {
            val result = repository.getFrostData(lat, lon, elements)
            _frostData.value = result
        }
    }
}