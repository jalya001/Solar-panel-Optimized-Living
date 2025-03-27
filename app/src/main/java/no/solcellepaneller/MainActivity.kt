package no.solcellepaneller

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import no.solcellepaneller.data.weatherdata.PVGISApi
import no.solcellepaneller.data.weatherdata.StartApiCall
import no.solcellepaneller.data.weatherdata.WeatherRepository
import no.solcellepaneller.data.weatherdata.WeatherViewModel
import no.solcellepaneller.ui.navigation.Nav
import no.solcellepaneller.ui.theme.SolcellepanellerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataSource = PVGISApi()
        val repository = WeatherRepository(dataSource)

        val testViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return WeatherViewModel(repository, lat = 59.943, long = 10.718, slope = 35) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        })[WeatherViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            SolcellepanellerTheme {
                StartApiCall(testViewModel) // Midlertidig test
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

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun AppPreview() {
    SolcellepanellerTheme {
        App()
    }
}