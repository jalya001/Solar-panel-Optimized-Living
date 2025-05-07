package no.solcellepanelerApp.model.onboarding

import no.solcellepanelerApp.R

sealed class OnBoardModel(val titleRes: Int) {
    data object FirstPage : OnBoardModel(R.string.onboard_title_1)
    data object SecondPage : OnBoardModel(R.string.onboard_title_2)
    data object ThirdPage : OnBoardModel(R.string.onboard_title_3)
    data object FourthPage : OnBoardModel(R.string.onboard_title_4)
}
