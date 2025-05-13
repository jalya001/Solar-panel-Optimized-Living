package no.SOL.ui.onboarding

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

    //110% mulig å gjøre alle disse overlaygreiene til en variabel og en funskjon men tidsklemme
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

    fun isDrawOverlayShown(): Boolean {
        return context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            .getBoolean("draw_overlay_shown", false)
    }

    fun setDrawOverlayShown() {
        context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("draw_overlay_shown", true)
            .apply()
    }

    fun isSavingsOverlayShown(): Boolean {
        return context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            .getBoolean("savings_overlay_shown", false)
    }

    fun setSavingsOverlayShown() {
        context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("savings_overlay_shown", true)
            .apply()
    }

    fun isHomeOverlayShown(): Boolean {
        return context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            .getBoolean("home_overlay_shown", false)
    }

    fun setHomeOverlayShown() {
        context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("home_overlay_shown", true)
            .apply()
    }

    fun isPriceOverlayShown(): Boolean {
        return context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            .getBoolean("price_overlay_shown", false)
    }

    fun setPriceOverlayShown() {
        context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("price_overlay_shown", true)
            .apply()
    }

    // warning here but only because it is for testing
    fun resetAllOnboardingStates() { //for testing
        context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("completed", false)
            .putBoolean("map_overlay_shown", false)
            .putBoolean("draw_overlay_shown", false)
            .putBoolean("savings_overlay_shown", false)
            .putBoolean("price_overlay_shown", false)
            .putBoolean("home_overlay_shown", false)
            .apply()
    }
}