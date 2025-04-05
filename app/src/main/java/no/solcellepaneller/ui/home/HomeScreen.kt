package no.solcellepaneller.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import no.solcellepaneller.R
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.HelpBottomSheet
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.font.FontScaleViewModel

@Composable
fun HomeScreen(navController: NavController,    fontScaleViewModel: FontScaleViewModel
) {
    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomBar(
            onHelpClicked = { showHelp = true },
            onAppearanceClicked = { showAppearance = true },
            navController = navController
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(id = R.string.home), style = MaterialTheme.typography.titleLarge)

            Button(onClick = { navController.navigate("map") }) { Text(stringResource(id = R.string.install_panels), style = MaterialTheme.typography.bodySmall) }
            Button(onClick = { navController.navigate("saved_locations") }) { Text(stringResource(id = R.string.saved_locations), style = MaterialTheme.typography.bodySmall) }
            Button(onClick = { navController.navigate("prices") }) { Text(stringResource(id = R.string.prices), style = MaterialTheme.typography.bodySmall) }

            HelpBottomSheet(visible = showHelp, navController = navController, onDismiss = { showHelp = false })
AppearanceBottomSheet(
    visible = showAppearance,
    onDismiss = { showAppearance = false },
    fontScaleViewModel = fontScaleViewModel
)        }
    }
}
