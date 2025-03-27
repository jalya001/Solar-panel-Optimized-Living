package no.solcellepaneller.ui.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import no.solcellepaneller.R
import no.solcellepaneller.data.homedata.ElectricityPriceRepository
import no.solcellepaneller.ui.additionalInput.AdditionalInputScreen
import no.solcellepaneller.ui.electricity.PriceScreen
import no.solcellepaneller.ui.home.HomeScreen
import no.solcellepaneller.ui.infoscreen.InfoScreen
import no.solcellepaneller.ui.map.MapScreen
import no.solcellepaneller.ui.map.MapScreenSimple
import no.solcellepaneller.ui.map.MapScreenViewModel
import no.solcellepaneller.ui.result.ResultScreen
import no.solcellepaneller.ui.savedLocations.SavedLocationsScreen

@Composable
fun Nav(navController: NavHostController) {
        val viewModel: MapScreenViewModel = viewModel()
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("map") {// val viewModel: MapScreenViewModel = viewModel()
            MapScreen(viewModel, navController)
        }
        composable("map_simple") { //val viewModel: MapScreenViewModel = viewModel()
            MapScreenSimple(viewModel, navController)
        }
        composable("additional_input") { AdditionalInputScreen(viewModel,navController) }
        composable("result") { ResultScreen(navController) }
        composable("saved_locations") { SavedLocationsScreen(navController) }
        composable("prices") {
            val mockRepository = ElectricityPriceRepository(
                priceArea = "mockPrice"
            )

            PriceScreen(
            repository = mockRepository,
            navController = navController,
            region = "Filler Region" //! TODO Finn ut en løsning her
        ) }
        composable("info_screen") { InfoScreen(navController)}
    }
}

@Composable
fun BottomBar(
    onHelpClicked: () -> Unit,
    onInfoClicked: () -> Unit,
    onAppearanceClicked: () -> Unit,
    navController: NavController
) {
    NavigationBar(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.tertiary,
    ) {

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Build, contentDescription = "Help") },
            label = { Text(stringResource(id = R.string.help)) },
            selected = false,
            onClick = onHelpClicked
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Info, contentDescription = "Information") },
            label = { Text("Info") },
            selected = false,
            onClick = { navController.navigate("info_screen") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Appearance") },
            label = { Text(stringResource(id = R.string.appereance)) },
            selected = false,
            onClick = onAppearanceClicked
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController,onBackClick: (() -> Unit)? = null) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            titleContentColor = MaterialTheme.colorScheme.tertiary,
            navigationIconContentColor = MaterialTheme.colorScheme.tertiary
        ),
        title = { Text(stringResource(id = R.string.go_back)) },
        navigationIcon = {
            IconButton(onClick = {
                onBackClick?.invoke()
                navController.popBackStack()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
            }
        }
    )
}