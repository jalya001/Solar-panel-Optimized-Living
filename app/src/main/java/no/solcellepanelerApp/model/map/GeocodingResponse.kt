package no.solcellepanelerApp.model.map


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponse(
    val placeId: Long,
    val licence: String,
    val osmType: String,
    val osmId: Long,
    val lat: String,
    val lon: String,
    val displayName: String,
    val boundingbox: List<String>,
    @SerialName("class") val className: String,
    val type: String,
    val importance: Double,
)