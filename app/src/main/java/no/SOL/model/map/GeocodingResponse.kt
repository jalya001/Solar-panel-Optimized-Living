package no.SOL.model.map


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// warnings are for variable name but cant change because of api call
@Serializable
data class GeocodingResponse(
    val place_id: Long,
    val licence: String,
    val osm_type: String,
    val osm_id: Long,
    val lat: String,
    val lon: String,
    val display_name: String,
    val boundingbox: List<String>,
    @SerialName("class") val className: String,
    val type: String,
    val importance: Double,
)