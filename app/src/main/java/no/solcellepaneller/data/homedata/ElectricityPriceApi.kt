package no.solcellepaneller.data.homedata

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.solcellepaneller.model.electricity.ElectricityPrice
import java.time.LocalDate

class ElectricityPriceApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun fetchPrices(date: LocalDate, priceArea: String): List<ElectricityPrice> {
        val url = "https://www.hvakosterstrommen.no/api/v1/prices/${date.year}/${date.monthValue}-${date.dayOfMonth}_$priceArea.json"
        return try {
            client.get(url).body()
        } catch (e: Exception) {
            println("Error fetching electricity prices: ${e.message}")
            emptyList()
        }
    }
}