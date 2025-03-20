package no.solcellepaneller.model.electricity

import kotlinx.serialization.Serializable

@Serializable
data class ElectricityPrice(
    val NOK_per_kWh: Double,
    val time_start: String,
    val time_end: String,
    val date: String,
    val region: String
) {
    fun getTimeRange(): String {
        return "$time_start - $time_end"
    }
}