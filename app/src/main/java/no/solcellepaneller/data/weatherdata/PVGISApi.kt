package no.solcellepaneller.data.weatherdata

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import no.solcellepaneller.model.weather.Energy
import java.net.HttpURLConnection
import java.net.URL

class PVGISDataSource {
    suspend fun getSolarEnergy(): List<Energy> = withContext(Dispatchers.IO){
        // Test adresse: Gaustadalléen 23B
        // Latitude: 59.943
        // Longitude: 10.718
        // Slope: 35
        // URL for grid connected solar energy: https://re.jrc.ec.europa.eu/api/v5_3/PVcalc?lat=59.943&lon=10.718&angle=35&azimuth=0&peakpower=1&loss=14&outputformat=json

        // Test verdier:
        val lat = 59.943
        val long = 10.718
        val slope = 35

        val apiUrl = "https://re.jrc.ec.europa.eu/api/v5_3/PVcalc?lat=$lat&lon=$long&angle=$slope&azimuth=0&peakpower=1&loss=14&outputformat=json"
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection

        return@withContext try {
            connection.requestMethod = "GET"
            connection.inputStream.bufferedReader().use { reader ->
                val jsonResponse = reader.readText()
                Log.d("PVGISApi", "JSON ResponseEnergy: $jsonResponse")

                val json = Json { ignoreUnknownKeys = true }
                val apiResponse = json.decodeFromString<ApiResponse>(jsonResponse)

                apiResponse.monthlyEnergy.map { it.toEnergy() }
            }
        } finally {
            connection.disconnect()
        }
    }

    // URL for monthly irradiation data horizontal radiation: https://re.jrc.ec.europa.eu/api/v5_3/MRcalc?lat=59.943&lon=10.718&angle=35&startyear=2023&endyear=2023&horirrad=1&outputformat=json
}

@Serializable
private data class ApiResponse(
    val monthlyEnergy: List<ApiEnergy>
)

@Serializable
private data class ApiEnergy(
    @SerialName("month") val date: Int,
    @SerialName("E_m") val energy: Double
) {
    fun toEnergy() = Energy(date, energy)
}