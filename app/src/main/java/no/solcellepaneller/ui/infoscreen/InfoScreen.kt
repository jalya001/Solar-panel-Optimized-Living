package no.solcellepaneller.ui.infoscreen


import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.solcellepaneller.R
import no.solcellepaneller.ui.navigation.AppearanceBottomSheet
import no.solcellepaneller.ui.navigation.BottomBar
import no.solcellepaneller.ui.navigation.HelpBottomSheet
import no.solcellepaneller.ui.navigation.TopBar
import no.solcellepaneller.ui.font.FontScaleViewModel
import no.solcellepaneller.ui.theme.SolcellepanellerTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(navController: NavController, fontScaleViewModel: FontScaleViewModel
) {
        var showHelp by remember { mutableStateOf(false) }
        var showAppearance by remember { mutableStateOf(false) }

        Scaffold(
            topBar = { TopBar(navController) },
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
                    content = {
                        Column {
                            Text(stringResource(id = R.string.intro_content), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                )

            }

            item {
                ExpandInfoSection(
                    title = stringResource(id = R.string.price_title),
                    content = {
                        Column {
                            Text(stringResource(id = R.string.price_content), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                )
            }
            item {

                ExpandInfoSection(
                    title = stringResource(id = R.string.pros_and_cons_title),
                    content = {
                        Column {
                            Text(stringResource(id = R.string.pros_and_cons_content), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                )

            }
            item {

                ExpandInfoSection(
                    title = stringResource(id = R.string.money_saved_title),
                    content = {
                        Column {
                            Text(stringResource(id = R.string.money_saved_content), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                )

            }
            item {

                ExpandInfoSection(
                    title = stringResource(id = R.string.cabin_title),
                    content = {
                        Column {
                            Text(stringResource(id = R.string.cabin_content), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                )

                }
            item {
                ExpandInfoSection(
                    title = stringResource(id = R.string.support_title),
                    content = {
                        Column {
                            Text(stringResource(id = R.string.support_content), style = MaterialTheme.typography.bodyMedium)
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
            HelpBottomSheet(visible = showHelp, navController = navController, onDismiss = { showHelp = false })
            AppearanceBottomSheet(
                visible = showAppearance,
                onDismiss = { showAppearance = false },
                fontScaleViewModel = fontScaleViewModel
            )
        }
}


@Composable
fun ExpandInfoSection(title: String,content: @Composable () -> Unit ){
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.elevatedCardColors(
        contentColor = MaterialTheme.colorScheme.tertiary,
        containerColor = MaterialTheme.colorScheme.secondary),
        onClick = {expanded = !expanded},
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    )
    {
        Column (modifier = Modifier.padding(16.dp)){

            Text(
                text = title, style = MaterialTheme.typography.titleMedium
            )

            if(expanded){
                content()
            }
        }
    }
}
