package no.SOL.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import no.SOL.R
import no.SOL.model.onboarding.OnBoardModel
import no.SOL.ui.theme.SOLTheme

//hentet fra https://medium.com/@samadtalukder/implement-an-intro-onboarding-screen-in-android-jetpack-compose-9f464de08b43

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {

    val pages = listOf(
        OnBoardModel.FirstPage,
        OnBoardModel.SecondPage,
        OnBoardModel.ThirdPage,
        OnBoardModel.FourthPage,
        OnBoardModel.FifthPage
    )

    val pagerState = rememberPagerState(initialPage = 0) {
        pages.size
    }

    val nextButton = stringResource(R.string.next)
    val backButton = stringResource(R.string.back)

    val buttonState = remember {
        derivedStateOf {
            when (pagerState.currentPage) {
                0 -> listOf("", nextButton)
                1 -> listOf(backButton, nextButton)
                2 -> listOf(backButton, nextButton)
                3 -> listOf(backButton, nextButton)
                4 -> listOf(backButton, "Start")
                else -> listOf("", "")
            }
        }
    }

    val scope = rememberCoroutineScope()

    Scaffold(bottomBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 10.dp, 10.dp, 60.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (buttonState.value[0].isNotEmpty()) {
                    ButtonUi(
                        text = buttonState.value[0],
                        backgroundColor = Color.Transparent,
                        textColor = Color.Gray
                    ) {
                        scope.launch {
                            if (pagerState.currentPage > 0) {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                IndicatorUI(pageSize = pages.size, currentPage = pagerState.currentPage)
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                ButtonUi(
                    text = buttonState.value[1],
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    textColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    scope.launch {
                        if (pagerState.currentPage < pages.size - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onFinished()
                        }
                    }
                }
            }

        }
    }, content = {
        Column(Modifier.padding(it)) {
            HorizontalPager(state = pagerState) { index ->
                OnboardingGraphUI(onBoardModel = pages[index])
            }
        }
    })


}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    SOLTheme(darkTheme = true) {
        OnboardingScreen {

        }
    }
}