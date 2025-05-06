package no.solcellepanelerApp.ui.reusables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
class AppScaffoldController {
    var topBarContent by mutableStateOf<(@Composable () -> Unit)?>(null)
    var topBarTitle by mutableStateOf<String?>(null)
    var isBottomBarVisible by mutableStateOf(true)

    val snackbarHostState = SnackbarHostState()

    fun setTopBar(title: String) {
        topBarTitle = title
        topBarContent = null
    }

    fun setCustomTopBar(content: @Composable () -> Unit) {
        topBarContent = content
        topBarTitle = null
    }

    fun clearTopBar() {
        topBarTitle = null
        topBarContent = null
    }

    fun clearBottomBar() {
        isBottomBarVisible = false
    }

    fun reinstateBottomBar() {
        isBottomBarVisible = true
    }

    suspend fun showSnackbar(message: String, actionLabel: String? = null) {
        snackbarHostState.showSnackbar(message = message, actionLabel = actionLabel)
    }
}
