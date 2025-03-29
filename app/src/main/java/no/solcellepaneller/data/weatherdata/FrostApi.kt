package no.solcellepaneller.data.weatherdata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FrostDataSource {
    suspend fun fetchFrostData(
        lat: Double,
        lon: Double,
        elements: List<String>
    ): Map<String, Array<Double>> {
        return withContext(Dispatchers.IO) {
            // Mock data
            val result = mutableMapOf<String, Array<Double>>()

            elements.forEach { element ->
                // filler values
                result[element] = Array(12) { (0..100).random().toDouble() }
            }
            result
        }
    }
}
