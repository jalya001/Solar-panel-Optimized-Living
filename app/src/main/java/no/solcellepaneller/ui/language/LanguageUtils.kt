package no.solcellepaneller.ui.language

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import no.solcellepaneller.R

//    https://www.youtube.com/watch?v=MXTsj43Csp4

object LanguageUtils {
    private const val PREFS_NAME = "language_prefs"
    private const val LANGUAGE_KEY = "language"

    fun setLanguage(context: Context, languageCode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)
                ?.applicationLocales = LocaleList.forLanguageTags(languageCode)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        }
        saveLanguage(context, languageCode)
    }

    fun saveLanguage(context: Context, languageCode: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(LANGUAGE_KEY, languageCode).apply()
    }

    fun getSavedLanguage(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(LANGUAGE_KEY, null)
    }
}

@Composable
fun langSwitch() {
    val context = LocalContext.current
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier.size(width = 240.dp, height = 60.dp),
        onClick = {
            val currentLang = LanguageUtils.getSavedLanguage(context) ?: "en"
            val newLang = if (currentLang == "en") "nb" else "en"
            LanguageUtils.setLanguage(context, newLang)
            (context as? Activity)?.recreate()
        }
    ) {
        Text(
            stringResource(id = R.string.language),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
        )
    }
}
