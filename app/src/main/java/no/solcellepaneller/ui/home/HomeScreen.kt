package no.solcellepaneller.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import no.solcellepaneller.R
import no.solcellepaneller.ui.font.FontScaleViewModel
import no.solcellepaneller.ui.handling.LoadingScreen
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.HelpBottomSheet
import no.solcellepaneller.ui.navigation.TopBar
import no.solcellepaneller.ui.reusables.MyNavCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController, fontScaleViewModel: FontScaleViewModel,
) {
    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    if (isLoading) {
        LoadingScreen()
        return
    }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                text = "*IKON og APPNAVN*",
                backClick = false,
            )
        },
        bottomBar = {
            BottomBar(
                onHelpClicked = { showHelp = true },
                onAppearanceClicked = { showAppearance = true },
                navController = navController
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            MyNavCard(
                text = stringResource(id = R.string.install_panels),
                route = "map",
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                style = MaterialTheme.typography.displayLarge, //Må bruke title for at the skal funke idw why
                content = { PanelAnimation() },
                color = MaterialTheme.colorScheme.tertiary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                MyNavCard(
                    text = stringResource(id = R.string.saved_locations),
                    route = "saved_locations",
                    navController = navController,
                    modifier = Modifier
                        .weight(1f)
                        .height(400.dp),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                MyNavCard(
                    text = stringResource(id = R.string.prices),
                    route = "prices",
                    navController = navController,
                    modifier = Modifier
                        .weight(1f)
                        .height(400.dp),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HelpBottomSheet(
                visible = showHelp,
                onDismiss = { showHelp = false },
            )
            AppearanceBottomSheet(
                visible = showAppearance,
                onDismiss = { showAppearance = false },
                fontScaleViewModel = fontScaleViewModel
            )

        }
    }
}

@Composable
fun PanelAnimation() {
    val animationFile = "solarPanel_anim.json"

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(animationFile)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    Box(modifier = Modifier.background(color = Color.Blue)) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .height(400.dp)
                .width(300.dp)
        )
    }
}

