package no.solcellepaneller.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Energy (
    @SerialName("month") val date: Int,
    @SerialName("E_m") val energy: Double
)