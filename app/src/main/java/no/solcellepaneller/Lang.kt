package no.solcellepaneller

import android.app.LocaleManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class Lang {
    fun getSavedLanguage(context: Context): String?{
        val sharedPreferences = context.getSharedPreferences("languge_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("language",null)
    }

    fun setLanguage(context: Context, languageCode: String) {
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
}