package no.solcellepanelerApp.model.map

data class AdressData(
    val adress: String,
    val lat: Double?,
    val lon: Double?,
    var slope: Double?,
    var efficiency: Double?,
)
