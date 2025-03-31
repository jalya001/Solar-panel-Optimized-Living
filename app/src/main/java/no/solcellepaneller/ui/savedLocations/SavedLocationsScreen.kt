package no.solcellepaneller.ui.savedLocations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.TopBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import no.solcellepaneller.R
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.HelpBottomSheet



@Composable
fun SavedLocationsScreen(navController: NavController) {
    var showHelp by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = {

 } ){ contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(id = R.string.saved_locations))

            HelpBottomSheet(visible = showHelp, navController = navController, onDismiss = { showHelp = false })
           
            AppearanceBottomSheet(visible = showAppearance, onDismiss = { showAppearance = false })
        }
    }
}
