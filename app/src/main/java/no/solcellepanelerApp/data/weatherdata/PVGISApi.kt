package no.solcellepanelerApp.data.weatherdata

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import no.solcellepanelerApp.model.weather.Radiation
import java.net.HttpURLConnection
import java.net.URL

class PVGISApi {
    suspend fun getRadiation(lat: Double, long: Double, slope: Int, azimuth: Int): List<Radiation> =
        withContext(Dispatchers.IO) {
            // Test adresse: Gaustadalléen 23B
            // Latitude: 59.943
            // Longitude: 10.718
            // Slope: 35
            // URL for grid connected solar energy: https://re.jrc.ec.europa.eu/api/v5_3/PVcalc?lat=59.943&lon=10.718&angle=35&azimuth=0&peakpower=1&loss=14&outputformat=json

            val apiUrl =
                "https://re.jrc.ec.europa.eu/api/v5_3/PVcalc?lat=$lat&lon=$long&angle=$slope&azimuth=azimuth&peakpower=1&loss=14&outputformat=json"
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection

            return@withContext try {
                connection.requestMethod = "GET"
                connection.inputStream.bufferedReader().use { reader ->
                    val jsonResponse = reader.readText()
                    Log.d("PVGISApi", "JSON ResponseEnergy: $jsonResponse")

                    val json = Json { ignoreUnknownKeys = true }
                    val apiResponse = json.decodeFromString<ApiResponse>(jsonResponse)

                    apiResponse.outputs.monthly.fixed.map { it.toRadiation() }
                }
            } finally {
                connection.disconnect()
            }
        }
}

@Serializable
private data class ApiResponse(
    val outputs: Outputs,
)

@Serializable
private data class Outputs(
    val monthly: Monthly,
)

@Serializable
private data class Monthly(
    val fixed: List<ApiVariables>,
)

@Serializable
private data class ApiVariables(
    @SerialName("month") val date: Int,
    @SerialName("E_m") val energy: Double,
    @SerialName("H(i)_m") val radiation: Double,
) {
    fun toRadiation() = Radiation(date, radiation)
}
