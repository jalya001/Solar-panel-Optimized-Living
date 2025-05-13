package no.SOL.data.weatherdata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class PVGISApi {
    @Serializable
    private data class PVGISResponse(
        val outputs: Outputs,
    )

    @Serializable
    private data class Outputs(
        val monthly: Monthly,
    )

    @Serializable
    private data class Monthly(
        val fixed: List<RadVariables>,
    )

    @Serializable
    private data class RadVariables(
        @SerialName("month") val date: Int,
        //@SerialName("E_m") val energy: Double,
        @SerialName("H(i)_m") val radiation: Double,
    )

    suspend fun getRadiation(
        client: CustomHttpClient,
        lat: Double, lon: Double,
        slope: Int,
        azimuth: Int,
    ): Result<Array<Double>> {
        /*=withContext(Dispatchers.IO)*/
        // Test adresse: Gaustadalléen 23B
        // Latitude: 59.943
        // Longitude: 10.718
        // Slope: 35
        // URL for grid connected solar energy: https://re.jrc.ec.europa.eu/api/v5_3/PVcalc?lat=59.943&lon=10.718&angle=35&azimuth=0&peakpower=1&loss=14&outputformat=json

        val url =
            "https://re.jrc.ec.europa.eu/api/v5_3/PVcalc?lat=$lat&lon=$lon&angle=$slope&azimuth=$azimuth&peakpower=1&loss=14&outputformat=json"
        println(url)

        val result: Result<PVGISResponse?> = client.httpRequest(url)
        val radiationData = Array(12) { 0.0 }
        result.onSuccess { body ->
            println("PVGIS: $body")
            if (body == null) return Result.failure(ApiException(ApiError.UNKNOWN_ERROR))
            body.outputs.monthly.fixed.forEach { radVariables ->
                radiationData[radVariables.date - 1] = radVariables.radiation
            }
            println(radiationData.contentToString())
            return Result.success(radiationData)
        }.onFailure { error ->
            if ((error as? ApiException)?.errorCode == ApiError.REQUEST_ERROR) return Result.failure(
                ApiException(ApiError.SEA_ERROR)
            )
            println("PVGIS: $error")
            return Result.failure(error)
        }
        return Result.failure(ApiException(ApiError.UNKNOWN_ERROR))
    }
}
