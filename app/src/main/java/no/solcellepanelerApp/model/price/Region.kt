package no.solcellepanelerApp.model.price

enum class Region(val displayName: String, val regionCode: String) {
    OSLO("Oslo/Øst-Norge", "NO1"),
    KRISTIANSAND("Kristiansand/Sør-Norge", "NO2"),
    TRONDHEIM("Trondheim/Midt-Norge", "NO3"),
    TROMSO("Tromsø/Nord-Norge", "NO4"),
    BERGEN("Bergen/Vest-Norge", "NO5")
}