package no.solcellepaneller.data.weatherdata

class FrostRepository(
    private val dataSource: FrostDataSource = FrostDataSource()
) {
    suspend fun getFrostData(
        lat: Double,
        lon: Double,
        elements: List<String>
    ): Map<String, Array<Double>> {
        return dataSource.fetchFrostData(lat, lon, elements)
    }
}