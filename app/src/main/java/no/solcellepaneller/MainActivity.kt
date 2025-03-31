package no.solcellepaneller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import no.solcellepaneller.data.homedata.ElectricityPriceRepository
import no.solcellepaneller.ui.electricity.PriceScreen
import no.solcellepaneller.ui.navigation.Nav
import no.solcellepaneller.ui.theme.SolcellepanellerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            SolcellepanellerTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    Nav(navController)
}