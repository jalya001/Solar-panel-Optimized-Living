package no.solcellepaneller.ui.font

import androidx.compose.runtime.mutableStateOf

object FontSizeState {
    // 1.0 = normal, >1 = larger, <1 = smaller
    var fontScale = mutableStateOf(1.0f)
}