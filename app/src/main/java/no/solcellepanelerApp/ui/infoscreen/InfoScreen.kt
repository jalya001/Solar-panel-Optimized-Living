package no.solcellepanelerApp.ui.infoscreen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.solcellepanelerApp.R
import no.solcellepanelerApp.ui.font.FontScaleViewModel
import no.solcellepanelerApp.ui.navigation.AppearanceBottomSheet
import no.solcellepanelerApp.ui.navigation.BottomBar
import no.solcellepanelerApp.ui.navigation.HelpBottomSheet
import no.solcellepanelerApp.ui.navigation.TopBar
import no.solcellepanelerApp.ui.reusables.ExpandInfoSection
import no.solcellepanelerApp.ui.reusables.ExpandInfoSectionContent


@Composable
fun InfoScreen(
    navController: NavController, fontScaleViewModel: FontScaleViewModel,
) {
    var showHelp by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }

    Scaffold(
        //topBar = { TopBar(navController, text = stringResource(R.string.info_title)) },
        topBar = {
            var backClicked by remember { mutableStateOf(false) }
            TopBar(
                navController = navController,
                text = stringResource(R.string.info_title),
                onBackClick = { backClicked = true },
                modifier = Modifier,
                backClick = !backClicked
            )
        },
        bottomBar = {
            BottomBar(
                onHelpClicked = { showHelp = true },
                onAppearanceClicked = { showAppearance = true },
                navController = navController
            )
        }
    ) { padding ->
        LazyColumn(
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
                ExpandInfoSectionContent(
                    title = stringResource(id = R.string.pros_and_cons_title),
                    content = {
                        val pros = listOf(
                            stringResource(R.string.solar_pro_1),
                            stringResource(R.string.solar_pro_2),
                            stringResource(R.string.solar_pro_3),
                            stringResource(R.string.solar_pro_4),
                            stringResource(R.string.solar_pro_5)
                        )
                        val cons = listOf(
                            stringResource(R.string.solar_con_1),
                            stringResource(R.string.solar_con_2),
                            stringResource(R.string.solar_con_3),
                            stringResource(R.string.solar_con_4),
                            stringResource(R.string.solar_con_5)
                        )
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                stringResource(R.string.pros_and_cons_content),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stringResource(R.string.pros),
                                style = MaterialTheme.typography.titleMedium
                            )
                            pros.forEach { point ->
                                Text(
                                    text = "• $point",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = stringResource(R.string.cons),
                                style = MaterialTheme.typography.titleMedium
                            )
                            cons.forEach { point ->
                                Text(
                                    text = "• $point",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
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
            item {
                ExpandInfoSectionContent(
                    title = stringResource(id = R.string.solar_panels_title),
                    content = {
                        Column {
                            Text(
                                stringResource(id = R.string.solar_panels_content),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            val uriHandler = LocalUriHandler.current
                            Text(
                                text = "\uD83C\uDF10 fusen.no/solcelleteknologi",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.clickable {
                                    uriHandler.openUri("https://blogg.fusen.no/alle/ulike-typer-solcelleteknologi")
                                }
                            )
                        }
                    }
                )
            }
            item {
                ExpandInfoSectionContent(
                    title = stringResource(id = R.string.support_title),
                    content = {
                        Column {
                            Text(
                                stringResource(id = R.string.support_content),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            val uriHandler = LocalUriHandler.current

                            Text(
                                text = "🌐 enova.no/privat",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.clickable {
                                    uriHandler.openUri("https://www.enova.no/privat")
                                }
                            )
                        }
                    }
                )
            }
        }

        HelpBottomSheet(
            navController = navController,
            visible = showHelp,
            onDismiss = { showHelp = false },
        )
        AppearanceBottomSheet(
            visible = showAppearance,
            onDismiss = { showAppearance = false },
            fontScaleViewModel = fontScaleViewModel
        )
    }

}