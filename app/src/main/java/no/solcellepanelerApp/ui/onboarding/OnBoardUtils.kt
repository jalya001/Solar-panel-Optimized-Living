package no.solcellepanelerApp.ui.onboarding

import android.content.Context

class OnboardingUtils(private val context: Context) {

    fun isOnboardingCompleted(): Boolean {
        return context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            .getBoolean("completed", false)
    }

    fun setOnboardingCompleted() {
        context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("completed", true)
            .apply()
    }

    fun isMapOverlayShown(): Boolean {
        return context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            .getBoolean("map_overlay_shown", false)
    }

    fun setMapOverlayShown() {
        context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("map_overlay_shown", true)
            .apply()
    }
}