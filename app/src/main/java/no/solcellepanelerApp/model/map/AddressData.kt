package no.solcellepanelerApp.model.map

data class AddressData(
    val address: String,
    val lat: Double?,
    val lon: Double?,
    var slope: Double?,
    var efficiency: Double?,
)
