package no.solcellepanelerApp.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Radiation(
    @SerialName("month") val date: Int,
    @SerialName("H(i)_m") val radiation: Double,
)