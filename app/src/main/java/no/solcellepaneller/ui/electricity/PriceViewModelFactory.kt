package no.solcellepaneller.ui.electricity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.solcellepaneller.data.homedata.ElectricityPriceRepository

class PriceViewModelFactory(
    private val repository: ElectricityPriceRepository,
    private val region: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PriceScreenViewModel::class.java)) {
            return PriceScreenViewModel(repository, region) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}