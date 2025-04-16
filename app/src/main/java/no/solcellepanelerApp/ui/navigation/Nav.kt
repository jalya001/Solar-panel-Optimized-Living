package no.solcellepanelerApp.ui.navigation


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import no.solcellepanelerApp.R
import no.solcellepanelerApp.data.homedata.ElectricityPriceRepository
import no.solcellepanelerApp.ui.electricity.PriceScreen
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.home.HomeScreen
import no.solcellepanelerApp.ui.infoscreen.InfoScreen
import no.solcellepanelerApp.ui.map.MapScreen
import no.solcellepanelerApp.ui.map.MapScreenViewModel
import no.solcellepanelerApp.ui.result.ResultScreen
import no.solcellepanelerApp.ui.result.ShowMonthlySavings
import no.solcellepanelerApp.ui.result.ShowYearlySavings
import no.solcellepanelerApp.ui.result.WeatherViewModel
import no.solcellepanelerApp.ui.savedLocations.SavedLocationsScreen

@Composable
fun Nav(navController: NavHostController, fontScaleViewModel: FontScaleViewModel) {
    val viewModel: MapScreenViewModel = viewModel()
    val WviewModel: WeatherViewModel = viewModel()
    val priceRepository = ElectricityPriceRepository("NO1")

    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController, fontScaleViewModel) }
        composable("map") {
            MapScreen(viewModel, navController, fontScaleViewModel, WviewModel)
        }
        composable("result") {
            ResultScreen(
                navController, viewModel, WviewModel, fontScaleViewModel,
                priceScreenViewModel = priceRepository //hvorfor heter den viewmodel hvis den tar en repo?
            )
        }
        composable("saved_locations") { SavedLocationsScreen(navController, fontScaleViewModel) }
        composable("prices") {
            val repository = ElectricityPriceRepository("NO1")
            PriceScreen(
                repository = repository,
                navController = navController, fontScaleViewModel
            )
        }
        composable("info_screen") { InfoScreen(navController, fontScaleViewModel) }
        composable(
            "monthly_savings/{month}/{energyProduced}/{energyPrice}",
            arguments = listOf(
                navArgument("month") { type = NavType.StringType },
                navArgument("energyProduced") { type = NavType.StringType },
                navArgument("energyPrice") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val month = backStackEntry.arguments?.getString("month") ?: ""
            val energyProduced =
                backStackEntry.arguments?.getString("energyProduced")?.toDoubleOrNull() ?: 0.0
            val energyPrice =
                backStackEntry.arguments?.getString("energyPrice")?.toDoubleOrNull() ?: 0.0

            ShowMonthlySavings(
                month,
                energyProduced,
                energyPrice,
                navController,
                fontScaleViewModel
            )
        }
        composable(
            "yearly_savings/{energyProduced}/{energyPrice}",
            arguments = listOf(
                navArgument("energyProduced") { type = NavType.StringType },
                navArgument("energyPrice") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val energyProduced =
                backStackEntry.arguments?.getString("energyProduced")?.toDoubleOrNull() ?: 0.0
            val energyPrice =
                backStackEntry.arguments?.getString("energyPrice")?.toDoubleOrNull() ?: 0.0

            ShowYearlySavings(energyProduced, energyPrice, navController, fontScaleViewModel)
        }
    }
}

@Composable
fun BottomBar(
    onHelpClicked: () -> Unit,
    onAppearanceClicked: () -> Unit,
    navController: NavController,
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(painterResource(R.drawable.help_24px), contentDescription = "Help") },
            label = { Text(stringResource(id = R.string.help)) },
            selected = false,
            onClick = onHelpClicked,
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.tertiary,
                unselectedTextColor = MaterialTheme.colorScheme.tertiary,
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(R.drawable.baseline_info_outline_24),
                    contentDescription = "Information"
                )
            },
            label = { Text("Info") },
            selected = false,
            //onClick = { navController.navigate("info_screen") },
            onClick = {
                if (navController.currentDestination?.route != "info_screen") {
                    navController.navigate("info_screen")
                }
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.tertiary,
                unselectedTextColor = MaterialTheme.colorScheme.tertiary,
            )
        )
        NavigationBarItem(//taktisk plassering innit, høyre tommel
            icon = { Icon(painterResource(R.drawable.home_24px), contentDescription = "Home") },
            label = { Text(stringResource(id = R.string.home_bottom_bar)) },
            selected = false,
            onClick = { navController.navigate("home") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.tertiary,
                unselectedTextColor = MaterialTheme.colorScheme.tertiary,
            )
        )
        NavigationBarItem(
//            icon = { Icon(Icons.Filled.Settings, contentDescription = "Appearance") },
            icon = {
                Icon(
                    painterResource(R.drawable.palette_24px),
                    contentDescription = "Appearance"
                )
            },
            label = { Text(stringResource(id = R.string.appearance)) },
            selected = false,
            onClick = onAppearanceClicked,
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.tertiary,
                unselectedTextColor = MaterialTheme.colorScheme.tertiary,
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    text: String,
    onBackClick: (() -> Unit)? = null,
    backClick: Boolean = true,
    modifier: Modifier = Modifier,
//    height: Dp = 90.dp
) {
    var backClicked by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        modifier = Modifier
            .fillMaxWidth(),
//            .height(height)
        title = {
            Text(
                text = text,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(top = 15.dp)
            )
        },
        navigationIcon = {
            if (backClick) {
//                OutlinedIconButton(
//                    onClick = {
//                        onBackClick?.invoke()
//                        navController.popBackStack()
//                    }, modifier = modifier.padding(top = 10.dp),
//                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary)
//                )
                OutlinedIconButton(
                    onClick = {
                        if (!backClicked) {
                            backClicked = true
                            onBackClick?.invoke()
                            navController.popBackStack()
                        }
                    },
                    enabled = !backClicked,
                    modifier = modifier.padding(top = 10.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            }
        }
    )
}

