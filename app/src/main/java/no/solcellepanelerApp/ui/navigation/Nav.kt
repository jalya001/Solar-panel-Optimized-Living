package no.solcellepanelerApp.ui.navigation


import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import no.solcellepanelerApp.R
import no.solcellepanelerApp.ui.home.HomeScreen
import no.solcellepanelerApp.ui.home.HomeTopBar
import no.solcellepanelerApp.ui.infoscreen.InfoScreen
import no.solcellepanelerApp.ui.map.MapScreen
import no.solcellepanelerApp.ui.onboarding.OnboardingScreen
import no.solcellepanelerApp.ui.price.PriceScreen
import no.solcellepanelerApp.ui.result.ResultScreen
import no.solcellepanelerApp.ui.savings.SavingsScreen
import no.solcellepanelerApp.ui.theme.isDarkThemeEnabled

@Composable
fun Nav(
    navController: NavHostController,
    appScaffoldController: AppScaffoldController,
    contentPadding: PaddingValues,
) {
    val titles = mapOf(
        "prices" to stringResource(R.string.price_title),
        "map" to stringResource(R.string.map_title),
        "info_screen" to stringResource(R.string.info_title),
        "result" to stringResource(R.string.results)
    )

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            val route = backStackEntry.destination.route
            when {
                route == "home" -> {
                    appScaffoldController.setCustomTopBar { HomeTopBar(isDarkTheme = isDarkThemeEnabled()) }
                    appScaffoldController.reinstateBottomBar()
                    appScaffoldController.enableOverlay("home")
                }

                route == "onboarding" -> {
                    appScaffoldController.clearTopBar()
                    appScaffoldController.clearBottomBar()
                }

                route in titles.keys -> {
                    appScaffoldController.setTopBar(titles[route] ?: "")
                    appScaffoldController.reinstateBottomBar()
                    appScaffoldController.enableOverlay(route ?: "")
                }

                route?.contains("savings", ignoreCase = true) == true -> {
                    appScaffoldController.enableOverlay("savings")
                }

                else -> {
                    appScaffoldController.clearTopBar()
                    appScaffoldController.clearBottomBar()
                }
            }
        }
    }

    NavHost(navController, startDestination = "home") {
        composable("onboarding") { OnboardingScreen(onFinished = { navController.popBackStack() }) }

        composable("home") {
            HomeScreen(
                navController,
                contentPadding = contentPadding
            )
        }
        composable("map") {
            MapScreen(
                navController,
                appScaffoldController,
                contentPadding = contentPadding
            )
        }
        composable("result") {
            ResultScreen(
                navController, contentPadding
            )
        }
        composable("prices") {
            PriceScreen(
                contentPadding = contentPadding
            )
        }
        composable("info_screen") {
            InfoScreen(
                contentPadding = contentPadding
            )
        }
        savingsComposable(
            route = "monthly_savings/{month}/{energyProduced}/{energyPrice}",
            isMonthly = true,
            appScaffoldController = appScaffoldController,
            contentPadding = contentPadding
        )
        savingsComposable(
            route = "yearly_savings/{energyProduced}/{energyPrice}",
            isMonthly = false,
            appScaffoldController = appScaffoldController,
            contentPadding = contentPadding
        )
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
            icon = {
                Icon(
                    painterResource(R.drawable.help_24px),
                    contentDescription = "Help"
                )
            },
            label = {
                Text(
                    stringResource(id = R.string.help),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
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
            label = { Text("Info", style = MaterialTheme.typography.bodyLarge) },
            selected = false,
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
            label = {
                Text(
                    stringResource(id = R.string.home_bottom_bar),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            selected = false,
            onClick = { navController.navigate("home") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.tertiary,
                unselectedTextColor = MaterialTheme.colorScheme.tertiary,
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(R.drawable.palette_24px),
                    contentDescription = "Appearance"
                )
            },
            label = {
                Text(
                    stringResource(id = R.string.appearance),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
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
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    showHomeButton: Boolean = false,
) {

    CenterAlignedTopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Text(
                text = text,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(top = 15.dp)
            )
        },
        navigationIcon = {
            OutlinedIconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = modifier.padding(top = 10.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        },
        actions = {
            if (showHomeButton) {
                OutlinedIconButton(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier.padding(top = 10.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary)
                ) {
                    Icon(
                        painterResource(R.drawable.home_24px),
                        contentDescription = "Home"
                    )
                }
            }
        }
    )
}

fun NavGraphBuilder.savingsComposable(
    route: String,
    isMonthly: Boolean,
    appScaffoldController: AppScaffoldController,
    contentPadding: PaddingValues,
) {
    val arguments = when (isMonthly) {
        true -> listOf(
            navArgument("month") { type = NavType.StringType },
            navArgument("energyProduced") { type = NavType.StringType },
            navArgument("energyPrice") { type = NavType.StringType }
        )

        false -> listOf(
            navArgument("energyProduced") { type = NavType.StringType },
            navArgument("energyPrice") { type = NavType.StringType }
        )
    }

    composable(route, arguments = arguments) { backStackEntry ->
        val month = backStackEntry.arguments?.getString("month") ?: ""
        val energyProduced =
            backStackEntry.arguments?.getString("energyProduced")?.toDoubleOrNull() ?: 0.0
        val energyPrice =
            backStackEntry.arguments?.getString("energyPrice")?.toDoubleOrNull() ?: 0.0

        SavingsScreen(
            isMonthly = isMonthly,
            month = if (isMonthly) month else "",
            energyProduced = energyProduced,
            energyPrice = energyPrice,
            appScaffoldController = appScaffoldController,
            contentPadding = contentPadding
        )
    }
}