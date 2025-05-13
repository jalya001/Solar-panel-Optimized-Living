package no.SOL.data.userdata

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.SOL.model.map.GeocodingResponse


class AddressApi {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { prettyPrint = true })
        }
    }

    suspend fun getCoordinates(address: String): List<GeocodingResponse> {
        val url = "https://geocode.maps.co/search"
        return try {
            // Make the API call
            val response: String = client.get(url) {
                parameter("q", address)
                parameter("api_key", "67dab2cc641e1191424678kjm036d35")
                parameter("format", "json")
            }.body()

            // Log the raw response for debugging
            Log.d("AddressDataSource", "Raw API response: $response")

            // Parse the JSON array directly into a List<GeocodingResponse>
            Json.decodeFromString<List<GeocodingResponse>>(response)
        } catch (e: Exception) {
            Log.e("AddressDataSource", "Error fetching coordinates", e)
            throw e  // Re-throw to allow proper error handling upstream
        }
    }


}
