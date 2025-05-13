package no.SOL.ui.reusables

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StateFlowDelegate<T>(initialValue: T) {
    private val _stateFlow = MutableStateFlow(initialValue)
    val stateFlow: StateFlow<T> get() = _stateFlow

    /*operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        _stateFlow.value = value
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = _stateFlow.value
    */
    var value: T
        get() = stateFlow.value
        set(newValue) {
            _stateFlow.value = newValue
        }
}
