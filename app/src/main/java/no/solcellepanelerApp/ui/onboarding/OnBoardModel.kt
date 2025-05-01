package no.solcellepanelerApp.ui.onboarding

import androidx.annotation.DrawableRes
import no.solcellepanelerApp.R

sealed class OnBoardModel(
    @DrawableRes val image: Int,
    val title: String,
    val description: String,
) {

    data object FirstPage : OnBoardModel(
        image = R.drawable.home_24px,
        title = "Your Reading Partner",
        description = "Read as many book as you want, anywhere you want"
    )

    data object SecondPage : OnBoardModel(
        image = R.drawable.home_24px,
        title = "Your Personal Library",
        description = "Organize books in different ways, make your own library"
    )

    data object ThirdPages : OnBoardModel(
        image = R.drawable.home_24px,
        title = "Search and Filter",
        description = "Get any book you want within a simple search across your device"
    )


}