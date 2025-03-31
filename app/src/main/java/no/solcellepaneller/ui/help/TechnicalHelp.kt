package no.solcellepaneller.ui.help


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import no.solcellepaneller.ui.navigation.TopBar
import no.solcellepaneller.ui.theme.SolcellepanellerTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnicalHelp(navController: NavController) {
    SolcellepanellerTheme {
        Scaffold(
            topBar = {
                TopBar(
                    navController
                )
            },

            ) { padding ->
            LazyColumn (
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {

                item {

                    ExpandInfoSection(
                        title = stringResource(id = R.string.tech_problems_title),
                        content =stringResource(id = R.string.tech_problems_content)
                    )
                }
            }
        }
    }}

//
//@Composable
//fun ExpandInfoSection(title: String,content: String ){
//    var expanded by remember { mutableStateOf(false) }
//
//    Card(
//        colors = CardDefaults.elevatedCardColors(
//        contentColor = MaterialTheme.colorScheme.tertiary,
//        containerColor = MaterialTheme.colorScheme.secondary),
//        onClick = {expanded = !expanded},
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//    )
//    {
//        Column (modifier = Modifier.padding(16.dp)){
//
//            Text(
//                text = title, style = MaterialTheme.typography.titleMedium
//            )
//
//            if(expanded){
//                Text(
//                    text = content,
//                    style = MaterialTheme.typography.bodyMedium,
//                    modifier = Modifier.padding(top = 8.dp)
//                )
//            }
//        }
//    }
//}
