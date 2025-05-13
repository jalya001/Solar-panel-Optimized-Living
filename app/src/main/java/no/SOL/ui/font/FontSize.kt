package no.SOL.ui.font

//import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf

//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue

object FontSizeState {
    var fontScale = mutableFloatStateOf(1.0f)
}

//object FontSizeManager {
//    private var _userFontScale by mutableStateOf<Float?>(null)
//    val userFontScale: Float? get() = _userFontScale
//
//    fun setCustomScale(scale: Float) {
//        _userFontScale = scale
//    }
//
//    fun resetToSystem() {
//        _userFontScale = null
//    }
//}
