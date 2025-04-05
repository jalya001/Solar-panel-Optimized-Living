package no.solcellepaneller.ui.font

import androidx.compose.runtime.mutableFloatStateOf
import androidx.lifecycle.ViewModel

class FontScaleViewModel : ViewModel() {
    val fontScale = mutableFloatStateOf(1f)

    fun increaseFontScale() {
        fontScale.floatValue += 0.1f
    }

    fun decreaseFontScale() {
        fontScale.floatValue = maxOf(0.5f, fontScale.floatValue - 0.1f)
    }

    fun resetFontScale() {
        fontScale.floatValue = 1.0f
    }

}
