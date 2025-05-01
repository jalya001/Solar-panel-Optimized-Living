package no.solcellepanelerApp.ui.onboarding

import no.solcellepanelerApp.R

sealed class OnBoardModel(val titleRes: Int) {
    object FirstPage : OnBoardModel(R.string.onboard_title_1)
    object SecondPage : OnBoardModel(R.string.onboard_title_2)
    object ThirdPage : OnBoardModel(R.string.onboard_title_3)
    object FourthPage : OnBoardModel(R.string.onboard_title_4)
}
