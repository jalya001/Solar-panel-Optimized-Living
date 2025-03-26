package no.solcellepaneller.data.weatherdata

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.solcellepaneller.model.weather.Energy

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _energyData = MutableLiveData<List<Energy>>(emptyList())
    val energyData: LiveData<List<Energy>> get() = _energyData

    init {
        fetchSolarEnergyInfo()
    }

    private fun fetchSolarEnergyInfo() {
        viewModelScope.launch{
            _energyData.value = repository.getSolarEnergyInfo()
            Log.d("WeatherViewModel", "Solar energy received: ${_energyData.value}")
        }
    }
}