package no.solcellepaneller

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import androidx.navigation.compose.rememberNavController
import no.solcellepaneller.data.homedata.ElectricityPriceRepository
import no.solcellepaneller.ui.electricity.PriceScreen
import no.solcellepaneller.ui.navigation.Nav
import no.solcellepaneller.ui.theme.SolcellepanellerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val languageCode=getSavedLanguage(this)?: "en"
        setLanguage(this,languageCode)
//        enableEdgeToEdge()
        setContent {
            SolcellepanellerTheme {
                App()
            }
        }
    }

    fun getSavedLanguage(context: Context): String?{
        val sharedPreferences = context.getSharedPreferences("languge_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("language",null)
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    Nav(navController)
    LazyColumn {
        items(10) {
            Text(
                text= stringResource(id =R.string.language)
            )
        }
        item {
            HorizontalDivider(Modifier.fillMaxWidth())
            langSwitch()
        }
    }
}
private fun setLanguage(context: Context, languageCode: String) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        context.getSystemService(LocaleManager::class.java)
            .applicationLocales=LocaleList.forLanguageTags(languageCode)
    }else{
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }
    saveLanguage(context,languageCode)
//    https://www.youtube.com/watch?v=MXTsj43Csp4
}
fun saveLanguage(context: Context, languageCode: String) {
    val sharedPreferences = context.getSharedPreferences("languge_prefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("language",languageCode).apply()
}

@Composable
fun langSwitch(){
    val context = LocalContext.current
    Column {
        Button(onClick = {
            setLanguage(context,"en")
        }) {
            Text("English")
        }
        Button(onClick = {
            setLanguage(context,"nb")
            (context as? Activity)?.recreate()
        }) {
            Text("Norsk")
        }
    }
}