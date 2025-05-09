package no.solcellepanelerApp.model.electricity

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import no.solcellepanelerApp.R

enum class Region(val regionCode: String) {
    OSLO("NO1"),
    KRISTIANSAND("NO2"),
    TRONDHEIM("NO3"),
    TROMSO("NO4"),
    BERGEN("NO5")
}

@Composable
fun getRegionName(region: Region): String {
    return when (region) {
        Region.OSLO -> stringResource(R.string.oslo)
        Region.KRISTIANSAND -> stringResource(R.string.kristiansand)
        Region.TRONDHEIM -> stringResource(R.string.trondheim)
        Region.TROMSO -> stringResource(R.string.tromso)
        Region.BERGEN -> stringResource(R.string.bergen)
    }
}