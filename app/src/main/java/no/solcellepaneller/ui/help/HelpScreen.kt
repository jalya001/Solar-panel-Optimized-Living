package no.solcellepaneller.ui.help


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
fun HelpScreen(navController: NavController, expandSection: String="",  fontScaleViewModel: FontScaleViewModel
) {
    SolcellepanellerTheme {
        var showHelp by remember { mutableStateOf(false) }
        var showAppearance by remember { mutableStateOf(false) }

        Scaffold(
            topBar = { TopBar(navController,text = stringResource(id=R.string.help)) },
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
                        title = stringResource(id = R.string.help_draw),

                        content = stringResource(id = R.string.how_to_draw),
                        initiallyExpanded = expandSection == "draw"
                    )
                }
                item {
                    ExpandInfoSection(
                        title = stringResource(id = R.string.tech_problems_title),
                        content =stringResource(id = R.string.tech_problems_content)
                    )
                }
            }
            AppearanceBottomSheet(
                visible = showAppearance,
                onDismiss = { showAppearance = false },
                fontScaleViewModel = fontScaleViewModel
            )
        }
    }}
