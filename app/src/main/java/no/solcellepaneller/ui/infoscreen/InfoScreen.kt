package no.solcellepaneller.ui.infoscreen


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.solcellepaneller.R
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.TopBar
import no.solcellepaneller.ui.font.FontScaleViewModel
import no.solcellepaneller.ui.reusables.ExpandInfoSection
import no.solcellepaneller.ui.theme.SolcellepanellerTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(navController: NavController, fontScaleViewModel: FontScaleViewModel
) {
    SolcellepanellerTheme {
        var showHelp by remember { mutableStateOf(false) }
        var showAppearance by remember { mutableStateOf(false) }

        Scaffold(
            topBar = { TopBar(navController, text = stringResource(R.string.info_title)) },
            bottomBar = {
                BottomBar(
                    onHelpClicked = { showHelp = true },
                    onAppearanceClicked = { showAppearance = true },
                    navController = navController
                )
            }
        ) { padding ->
        LazyColumn (
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            item {

                ExpandInfoSection(
                    title = stringResource(id = R.string.intro_title),
                    content = stringResource(id = R.string.intro_content)
                )

            }

            item {
                ExpandInfoSection(
                    title = stringResource(id = R.string.price_title),
                    content = stringResource(id = R.string.price_content)
                )
            }
            item {

                ExpandInfoSection(
                    title = stringResource(id = R.string.pros_and_cons_title),
                    content = stringResource(id = R.string.pros_and_cons_content)
                )

            }
            item {

                ExpandInfoSection(
                    title = stringResource(id = R.string.money_saved_title),
                    content = stringResource(id = R.string.money_saved_content)
                )

            }
            item {

                ExpandInfoSection(
                    title = stringResource(id = R.string.cabin_title),
                    content = stringResource(id = R.string.cabin_content)
                )

                }
            }
            AppearanceBottomSheet(
                visible = showAppearance,
                onDismiss = { showAppearance = false },
                fontScaleViewModel = fontScaleViewModel
            )
        }
    }
}