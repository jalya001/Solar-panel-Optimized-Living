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
import no.solcellepaneller.ui.electricity.PriceScreen
import no.solcellepaneller.ui.home.HomeScreen
import no.solcellepaneller.ui.infoscreen.InfoScreen
import no.solcellepaneller.ui.map.MapScreen
import no.solcellepaneller.ui.map.MapScreenViewModel
import no.solcellepaneller.ui.result.ResultScreen
import no.solcellepaneller.ui.savedLocations.SavedLocationsScreen
import androidx.navigation.navArgument
import no.solcellepaneller.ui.help.AppHelp
import no.solcellepaneller.ui.help.TechnicalHelp
import no.solcellepaneller.ui.result.WeatherViewModel
import no.solcellepaneller.ui.font.FontScaleViewModel

@Composable
fun Nav(navController: NavHostController, fontScaleViewModel: FontScaleViewModel) {
        val viewModel: MapScreenViewModel = viewModel()
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController,fontScaleViewModel) }
        composable("map") {
            MapScreen(viewModel, navController)
        }
        composable("result") {
            val WviewModel: WeatherViewModel = viewModel()
            ResultScreen(navController,viewModel,WviewModel,fontScaleViewModel) }
        composable("saved_locations") { SavedLocationsScreen(navController,fontScaleViewModel) }
        composable("prices") {
            val repository = ElectricityPriceRepository("NO1")
            PriceScreen(
            repository = repository,
            navController = navController,fontScaleViewModel
        ) }
        composable("info_screen") { InfoScreen(navController,fontScaleViewModel)}
//        composable("app_help") { AppHelp(navController) }
        composable(
            "app_help?expandSection={expandSection}",
            arguments = listOf(
                navArgument("expandSection") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val expandSection = backStackEntry.arguments?.getString("expandSection") ?: ""
            AppHelp(navController, expandSection,fontScaleViewModel)
        }
        composable("tech_help") { TechnicalHelp(navController,fontScaleViewModel) }
    }
}

@Composable
fun BottomBar(
    onHelpClicked: () -> Unit,
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