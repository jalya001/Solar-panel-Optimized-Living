package no.solcellepanelerApp.ui.navigation

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import no.solcellepanelerApp.R
import no.solcellepanelerApp.ui.onboarding.OnboardingUtils

class AppScaffoldController(context: Context) {
    private val onboardingUtils = OnboardingUtils(context)

    private data class OverlayEntry(
        @StringRes val titleRes: Int,
        @StringRes val bodyRes: Int,
        val hasShown: () -> Boolean,
        val setShown: (Boolean) -> Unit
    )

    private val overlayEntries: Map<String, OverlayEntry> = mapOf(
        "home" to OverlayEntry(
            titleRes = R.string.home_overlay_title,
            bodyRes = R.string.home_overlay,
            hasShown = { onboardingUtils.isHomeOverlayShown() },
            setShown = { onboardingUtils.setHomeOverlayShown() }
        ),
        "prices" to OverlayEntry(
            titleRes = R.string.price_overlay_title,
            bodyRes = R.string.price_overlay,
            hasShown = { onboardingUtils.isPriceOverlayShown() },
            setShown = { onboardingUtils.setPriceOverlayShown() }
        ),
        "map" to OverlayEntry(
            titleRes = R.string.map_overlay_title,
            bodyRes = R.string.map_overlay,
            hasShown = { onboardingUtils.isMapOverlayShown() },
            setShown = { onboardingUtils.setMapOverlayShown() }
        ),
        "draw" to OverlayEntry(
            titleRes = R.string.draw_overlay_title,
            bodyRes = R.string.map_draw_overlay,
            hasShown = { onboardingUtils.isDrawOverlayShown() },
            setShown = { onboardingUtils.setDrawOverlayShown() }
        ),
        "savings" to OverlayEntry(
            titleRes = R.string.saving_overlay_title,
            bodyRes = R.string.saving_overlay,
            hasShown = { onboardingUtils.isSavingsOverlayShown() },
            setShown = { onboardingUtils.setSavingsOverlayShown() }
        ),
    )

    var topBarContent by mutableStateOf<(@Composable () -> Unit)?>(null)
    var topBarTitle by mutableStateOf<String?>(null)
    var isBottomBarVisible by mutableStateOf(true)

    var showHelp by mutableStateOf(false)
    var showAppearance by mutableStateOf(false)

    var showOverlay by mutableStateOf(false)
    var overlayTitle by mutableStateOf(-1)
    var overlayBody by mutableStateOf(-1)

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

    fun enableOverlay(overlay: String) {
        val entry = overlayEntries[overlay]
        Log.d("AppScaffoldController","Overlay is $overlay")
        //if (entry != null) Log.d("AppScaffoldController","Has shown? is ${entry.hasShown()}")
        if (entry != null && !entry.hasShown()) {
            overlayTitle = entry.titleRes
            overlayBody = entry.bodyRes
            showOverlay = true
            entry.setShown(true)
        }
        Log.d("AppScaffoldController","Show overlay is $showOverlay")
    }
}
