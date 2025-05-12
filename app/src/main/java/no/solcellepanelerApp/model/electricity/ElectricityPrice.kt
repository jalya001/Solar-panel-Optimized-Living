package no.solcellepanelerApp.model.electricity

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// Data class representing the electricity price for a specific hour
@Serializable
data class ElectricityPrice(
    val NOK_per_kWh: Double,    // Price in NOK
    val EUR_per_kWh: Double,    // Price in EUR
    val EXR: Double,            // Exchange rate (EUR to NOK)

    val time_start: String,     // Start time (ISO 8601 format)
    val time_end: String,       // End time (ISO 8601 format)
    val region: String = "",    // Electricity price region
    val date: String = LocalDate.now().toString() // Date the price applies to
) {
    // Returns formatted string showing the time range
    fun getTimeRange(): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale("no"))
        val start = ZonedDateTime.parse(time_start).format(formatter)
        val end = ZonedDateTime.parse(time_end).format(formatter)
        return "$start - $end"
    }
}