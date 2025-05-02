package no.solcellepanelerApp.ui.result

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.solcellepanelerApp.R
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.navigation.AppearanceBottomSheet
import no.solcellepanelerApp.ui.navigation.BottomBar
import no.solcellepanelerApp.ui.navigation.HelpBottomSheet
import no.solcellepanelerApp.ui.navigation.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowMonthlySavings(
    month: String,
    energyProduced: Double,
    energyPrice: Double,
    navController: NavController,
    fontScaleViewModel: FontScaleViewModel,
    weatherViewModel: WeatherViewModel,
) {
    val savings: Double = energyProduced * energyPrice
    val weather by weatherViewModel.weatherData.collectAsState()

    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                navController,
                text = stringResource(R.string.monthly_savings, month)
            )
        },
        bottomBar = {
            BottomBar(
                onHelpClicked = { showHelp = true },
                onAppearanceClicked = { showAppearance = true },
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = MaterialTheme.typography.headlineSmall.toSpanStyle().copy(
                            fontWeight = FontWeight.ExtraLight
                        )
                    ) {
                        append(stringResource(R.string.monthly_savings_prefix))
                    }
                    append(
                        AnnotatedString(
                            String.format(" %.2f kroner ", savings),
                            MaterialTheme.typography.headlineSmall.toSpanStyle()
                        )
                    )
                    withStyle(
                        style = MaterialTheme.typography.headlineSmall.toSpanStyle().copy(
                            fontWeight = FontWeight.ExtraLight
                        )
                    ) {
                        append(stringResource(R.string.monthly_savings_suffix_part1))
                        append(month)
                        append(stringResource(R.string.monthly_savings_suffix_part2))
                    }
                },
                textAlign = TextAlign.Center
            )

            ShowProduce(
                energyProduced,
                weather, navController, fontScaleViewModel
            )

            HelpBottomSheet(
                navController = navController,
                visible = showHelp,
                onDismiss = { showHelp = false })
            AppearanceBottomSheet(
                visible = showAppearance,
                onDismiss = { showAppearance = false },
                fontScaleViewModel = fontScaleViewModel
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun ShowYearlySavings(
    energyProduced: Double,
    energyPrice: Double,
    navController: NavController,
    fontScaleViewModel: FontScaleViewModel,
    weatherViewModel: WeatherViewModel,
) {
    val savings: Double = energyProduced * energyPrice
    val weather by weatherViewModel.weatherData.collectAsState()
    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                navController,
                text = stringResource(R.string.yearly_savings)
            )
        },
        bottomBar = {
            BottomBar(
                onHelpClicked = { showHelp = true },
                onAppearanceClicked = { showAppearance = true },
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = MaterialTheme.typography.headlineSmall.toSpanStyle().copy(
                            fontWeight = FontWeight.ExtraLight
                        )
                    ) {
                        append(stringResource(R.string.yearly_savings_prefix))
                    }
                    append(
                        AnnotatedString(
                            String.format(" %.2f kroner ", savings),
                            MaterialTheme.typography.headlineSmall.toSpanStyle()
                        )
                    )
                    withStyle(
                        style = MaterialTheme.typography.headlineSmall.toSpanStyle().copy(
                            fontWeight = FontWeight.ExtraLight
                        )
                    ) {
                        append(stringResource(R.string.yearly_savings_suffix))
                    }
                },
                textAlign = TextAlign.Center
            )
            ShowProduce(
                energyProduced,
                weather, navController, fontScaleViewModel
            )


            HelpBottomSheet(
                navController = navController,
                visible = showHelp,
                onDismiss = { showHelp = false })
            AppearanceBottomSheet(
                visible = showAppearance,
                onDismiss = { showAppearance = false },
                fontScaleViewModel = fontScaleViewModel
            )
        }
    }
}