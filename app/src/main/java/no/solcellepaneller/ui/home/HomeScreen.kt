package no.solcellepaneller.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.solcellepaneller.R
import no.solcellepaneller.ui.font.FontScaleViewModel
import no.solcellepaneller.ui.handling.LoadingScreen
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.HelpBottomSheet
import no.solcellepaneller.ui.navigation.TopBar
import no.solcellepaneller.ui.reusables.MyNavCard

@Composable
fun HomeScreen(
    navController: NavController,
    fontScaleViewModel: FontScaleViewModel
) {
    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    if (isLoading) {
        LoadingScreen()
        return
    }

    Scaffold(
        topBar = { TopBar(navController=navController, text ="*IKON og APPNAVN*" ,backClick = false, height = 150.dp) },
        bottomBar = {
            BottomBar(
            onHelpClicked = { showHelp = true },
            onAppearanceClicked = { showAppearance = true },
            navController = navController
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding).padding(5.dp),
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
                style = "Large"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                MyNavCard(
                    text = stringResource(id = R.string.saved_locations),
                    route = "saved_locations",
                    navController = navController,
                    modifier = Modifier.weight(1f).height(400.dp),
                    style = "Large"
                )

                MyNavCard(
                    text = stringResource(id = R.string.prices),
                    route = "prices",
                    navController = navController,
                    modifier = Modifier.weight(1f)
                        .height(400.dp),
                    style = "Large"
                )
            }

            HelpBottomSheet(
                visible = showHelp,
                onDismiss ={ showHelp = false },
            )

            AppearanceBottomSheet(
                visible = showAppearance,
                onDismiss = { showAppearance = false },
                fontScaleViewModel = fontScaleViewModel
            )
        }
    }
}
