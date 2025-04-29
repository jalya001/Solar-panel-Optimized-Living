package no.solcellepanelerApp.data.mapdata

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*

class OpenElevationApi {
    @Serializable
    data class ElevationLocation(val latitude: Double, val longitude: Double, val elevation: Double)

    @Serializable
    data class ElevationResponse(val results: List<ElevationLocation>)

    suspend fun fetchElevation(coordinates: Pair<Double, Double>): Double? {
        val client = HttpClient(CIO) { // TBD: Make a reusable client
            install(ContentNegotiation) {
                json()
            }
        }

        try {
            val response: ElevationResponse =
                client.get("https://api.open-elevation.com/api/v1/lookup") {
                    url {
                        parameters.append("locations", "${String.format(java.util.Locale.US, "%.7f", coordinates.first).toDouble()},${String.format(java.util.Locale.US, "%.7f", coordinates.second).toDouble()}")
                    }
                }.body()


            val result = response.results.firstOrNull()
            println("Elevation: ${result?.elevation} meters")
            return result?.elevation
        } catch (e: Exception) {
            throw e
        }
    }
}