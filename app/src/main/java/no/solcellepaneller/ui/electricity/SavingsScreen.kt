package no.solcellepaneller.ui.electricity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.solcellepaneller.ui.font.FontScaleViewModel
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.HelpBottomSheet
import no.solcellepaneller.ui.navigation.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowMonthlySavings(month: String, energyProduced: Double, energyPrice: Double, navController: NavController, fontScaleViewModel: FontScaleViewModel){
    val savings: Double = energyProduced * energyPrice
    val fontScale = fontScaleViewModel.fontScale.floatValue.toFloat()

    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold (
        topBar = { TopBar(navController) },
        bottomBar = {
            BottomBar(
                onHelpClicked = { showHelp = true },
                onAppearanceClicked = { showAppearance = true },
                navController = navController
            )
        }
    ){ paddingValues ->
        Column (
            modifier = Modifier.fillMaxWidth().padding(12.dp).padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hvor mye vil du spare?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Du vil spare %.2f kroner i $month på strøm fra solcellepaneler".format(savings, month),
                fontSize = 18.sp * fontScale,
                textAlign = TextAlign.Center
            )

            HelpBottomSheet(visible = showHelp, navController = navController, onDismiss = { showHelp = false })
            AppearanceBottomSheet(visible = showAppearance, onDismiss = { showAppearance = false }, fontScaleViewModel = fontScaleViewModel)
        }
    }
}

@Composable
fun ShowYearlySavings(energyProduced: Double, energyPrice: Double, navController: NavController, fontScaleViewModel: FontScaleViewModel){
    val savings: Double = energyProduced * energyPrice
    val fontScale = fontScaleViewModel.fontScale.floatValue.toFloat()

    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold (
        topBar = {TopBar(navController)},
        bottomBar = {
            BottomBar(
                onHelpClicked = {showHelp = true},
                onAppearanceClicked = {showAppearance = true},
                navController = navController
            )
        }
    ){ paddingValues ->
        Column (
            modifier = Modifier.fillMaxWidth().padding(12.dp).padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Hvor mye vil du spare årlig?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Du vil spare %.2f kroner i året på strøm fra solcellepaneler".format(savings),
                fontSize = 18.sp * fontScale,
                textAlign = TextAlign.Center
            )

            HelpBottomSheet(visible = showHelp, navController = navController, onDismiss = { showHelp = false })
            AppearanceBottomSheet(visible = showAppearance, onDismiss = { showAppearance = false }, fontScaleViewModel = fontScaleViewModel)
        }
    }
}