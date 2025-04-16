package no.solcellepanelerApp.ui.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
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
) {
    val savings: Double = energyProduced * energyPrice
    val fontScale = fontScaleViewModel.fontScale.floatValue.toFloat()

    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar(navController, text = stringResource(R.string.monthly_savings, month)) },
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
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.monthly_savings_text, savings, month),
                fontSize = 18.sp * fontScale,
                textAlign = TextAlign.Center
            )

            ShowProduce(energyProduced, navController, fontScaleViewModel)

            HelpBottomSheet(visible = showHelp, onDismiss = { showHelp = false })
            AppearanceBottomSheet(
                visible = showAppearance,
                onDismiss = { showAppearance = false },
                fontScaleViewModel = fontScaleViewModel
            )
        }
    }
}

@Composable
fun ShowYearlySavings(
    energyProduced: Double,
    energyPrice: Double,
    navController: NavController,
    fontScaleViewModel: FontScaleViewModel,
) {
    val savings: Double = energyProduced * energyPrice
    val fontScale = fontScaleViewModel.fontScale.floatValue.toFloat()

    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar(navController, text = stringResource(R.string.yearly_savings)) },
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
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.yearly_savings_text, savings),
                fontSize = 18.sp * fontScale,
                textAlign = TextAlign.Center,
            )

            ShowProduce(energyProduced, navController, fontScaleViewModel)

            HelpBottomSheet(visible = showHelp, onDismiss = { showHelp = false })
            AppearanceBottomSheet(
                visible = showAppearance,
                onDismiss = { showAppearance = false },
                fontScaleViewModel = fontScaleViewModel
            )
        }
    }
}