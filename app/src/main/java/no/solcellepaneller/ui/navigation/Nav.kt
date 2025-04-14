package no.solcellepaneller.ui.navigation


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
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
import no.solcellepaneller.ui.electricity.ShowMonthlySavings
import no.solcellepaneller.ui.electricity.ShowYearlySavings
import no.solcellepaneller.ui.result.WeatherViewModel
import no.solcellepaneller.ui.font.FontScaleViewModel
import androidx.compose.material3.OutlinedIconButton
import no.solcellepaneller.ui.result.ShowProduce

@Composable
fun Nav(navController: NavHostController, fontScaleViewModel: FontScaleViewModel) {
        val viewModel: MapScreenViewModel = viewModel()
    val WviewModel: WeatherViewModel = viewModel()
    val priceRepository = ElectricityPriceRepository("NO1")

    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController,fontScaleViewModel) }
        composable("map") {
            MapScreen(viewModel, navController,fontScaleViewModel,WviewModel)
        }
        composable("result") {
            ResultScreen(
                navController, viewModel, WviewModel, fontScaleViewModel,
                priceScreenViewModel = priceRepository //hvorfor heter den viewmodel hvis den tar en repo?
            ) }
        composable("saved_locations") { SavedLocationsScreen(navController,fontScaleViewModel) }
        composable("prices") {
            val repository = ElectricityPriceRepository("NO1")
            PriceScreen(
            repository = repository,
            navController = navController,fontScaleViewModel
        ) }
        composable("info_screen") { InfoScreen(navController,fontScaleViewModel)}
        composable("produce/{energy}") { backStackEntry ->
            val energy = backStackEntry.arguments?.getString("energy")?.toDoubleOrNull() ?: 0.0
            ShowProduce(
                energy = energy,
                navController = navController,
                fontScaleViewModel = fontScaleViewModel
            )
        }
        composable(
            "monthly_savings/{month}/{energyProduced}/{energyPrice}",
            arguments = listOf(
                navArgument ("month") { type = NavType.StringType },
                navArgument ("energyProduced") { type = NavType.StringType },
                navArgument ("energyPrice") { type = NavType.StringType }
            )
        ) {backStackEntry ->
            val month = backStackEntry.arguments?.getString("month") ?: ""
            val energyProduced = backStackEntry.arguments?.getString("energyProduced")?.toDoubleOrNull() ?: 0.0
            val energyPrice = backStackEntry.arguments?.getString("energyPrice")?.toDoubleOrNull() ?: 0.0

            ShowMonthlySavings(month, energyProduced, energyPrice, navController, fontScaleViewModel)
        }
        composable ("yearly_savings/{energyProduced}/{energyPrice}",
        arguments = listOf(
            navArgument ("energyProduced") { type = NavType.StringType },
            navArgument ("energyPrice") { type = NavType.StringType }
        )
        ){ backStackEntry ->
            val energyProduced = backStackEntry.arguments?.getString("energyProduced")?.toDoubleOrNull() ?: 0.0
            val energyPrice = backStackEntry.arguments?.getString("energyPrice")?.toDoubleOrNull() ?: 0.0

            ShowYearlySavings(energyProduced, energyPrice, navController, fontScaleViewModel)
        }
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
fun TopBar(
    navController: NavController,
    text: String = "",
    onBackClick: (() -> Unit)? = null,
    backClick: Boolean = true,
    modifier: Modifier = Modifier,
    height: Dp = 90.dp
) {
    Box(modifier = modifier.fillMaxWidth()) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth().height(height),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                titleContentColor = MaterialTheme.colorScheme.tertiary,
                navigationIconContentColor = MaterialTheme.colorScheme.tertiary
            ),
            title = {},
            navigationIcon = {
                if (backClick) {
                    OutlinedIconButton(onClick = {
                        onBackClick?.invoke()
                        navController.popBackStack()
                     },modifier=modifier.padding(top = 10.dp),
                        border = BorderStroke(2.dp,MaterialTheme.colorScheme.tertiary)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height).padding(top = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = Bold
            )
        }
    }
}

