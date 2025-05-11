package no.solcellepanelerApp.data.pricedata

import android.annotation.SuppressLint
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.solcellepanelerApp.model.price.ElectricityPrice
import java.time.ZonedDateTime

class ElectricityPriceApi {
    //Initialize an HTTP client with JSON support
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            }) //Ignore any unexpected fields in the JSON response
        }
    }

    //Fetches electricity prices for a specific date and price area
    @SuppressLint("DefaultLocale")
    suspend fun fetchPrices(date: ZonedDateTime, priceArea: String): List<ElectricityPrice> {
        //Format the date properly to match the API's expected URL structure (e.g., 2025/04-04_NO1.json)
        val formattedMonth = String.format("%02d", date.monthValue)
        val formattedDay = String.format("%02d", date.dayOfMonth)

        val url =
            "https://www.hvakosterstrommen.no/api/v1/prices/${date.year}/${formattedMonth}-${formattedDay}_$priceArea.json"
        Log.d("ElectricityPriceApi", "Fetching data from URL: $url")
        //Try to fetch data from the API, return it as a list of ElectricityPrice objects
        return try {
            client.get(url).body()
        } catch (e: Exception) {
            //If anything fails (network error, parsing, etc.), log the error and return an empty list
            println("Error fetching electricity prices: ${e.message}")
            emptyList()
        }
    }
}