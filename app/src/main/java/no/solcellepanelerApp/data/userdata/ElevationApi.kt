package no.solcellepanelerApp.data.userdata

import com.google.android.gms.maps.model.LatLng
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

class ElevationApi {
    suspend fun fetchElevation(coordinates: LatLng): Double? {
        val client = HttpClient(CIO) { // TBD: Make a reusable client
            install(ContentNegotiation) {
                json()
            }
        }

        try {
            val latitude = String.format(java.util.Locale.US, "%.7f", coordinates.latitude)
            val longitude = String.format(java.util.Locale.US, "%.7f", coordinates.longitude)
            val url = "https://www.elevation-api.eu/v1/elevation/$latitude/$longitude"

            println(url)

            val response: String = client.get(url).body()
            val elevation: Double? = response.trim().toDoubleOrNull()

            println("Elevation: $elevation meters")
            return elevation
        } catch (e: Exception) {
            println("Error fetching elevation: ${e.message}")
            return null
        }
    }
}