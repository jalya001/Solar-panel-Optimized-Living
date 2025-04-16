package no.solcellepanelerApp.ui.font

import androidx.compose.runtime.mutableFloatStateOf
import androidx.lifecycle.ViewModel

class FontScaleViewModel : ViewModel() {
    val fontScale = mutableFloatStateOf(1f)

    fun increaseFontScale(): Boolean {
        return if (fontScale.floatValue < 1.6f) {
            fontScale.floatValue += 0.1f
            true
        } else {
            false
        }
    }

    fun decreaseFontScale(): Boolean {
        return if (fontScale.floatValue > 1.0f) {
            fontScale.floatValue -= 0.1f
            true
        } else {
            false
        }
    }

    fun resetFontScale() {
        fontScale.floatValue = 1.0f
    }

}
