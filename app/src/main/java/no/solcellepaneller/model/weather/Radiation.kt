package no.solcellepaneller.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Radiation (
    @SerialName("month") val date: Int,
    @SerialName("H(h)_m") val radiation: Double
)