package no.solcellepaneller.model.electricity

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Serializable
data class ElectricityPrice(
    val NOK_per_kWh: Double,
    val EUR_per_kWh: Double,
    val EXR: Double,
    val time_start: String,
    val time_end: String,
    val region: String = "",
    val date: String = LocalDate.now().toString()
) {
    fun getTimeRange(): String {
        val formatter = DateTimeFormatter.ofPattern("d. MMM yyyy, HH:mm", Locale("no"))
        val start = ZonedDateTime.parse(time_start).format(formatter)
        val end = ZonedDateTime.parse(time_end).format(formatter)
        return "$start - $end"
    }
}