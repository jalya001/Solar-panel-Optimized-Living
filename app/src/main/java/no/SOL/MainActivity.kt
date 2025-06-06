package no.SOL

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val client = OkHttpClient()

    companion object {
        init {
            System.loadLibrary("SOL")
        }
    }

    external fun replaceVowelsWithK(buffer: ByteBuffer)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            try {
                val url = "https://httpbin.org/json"

                val buffer = fetchIntoExactBuffer(url)

                buffer.flip()

                replaceVowelsWithK(buffer)

                val kCount = countKs(buffer)
                println("Number of 'k's in buffer: $kCount")

                val modifiedBytes = ByteArray(buffer.remaining())
                buffer.get(modifiedBytes)

                val modifiedString = String(modifiedBytes)
                println("Modified string:\n$modifiedString")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun fetchIntoExactBuffer(url: String): ByteBuffer = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            //if (!response.isSuccessful) throw Exception("HTTP error ${response.code}")

            val contentLength = response.body?.contentLength() ?: -1L
            if (contentLength <= 0 || contentLength > Int.MAX_VALUE) {
                throw Exception("Invalid or unknown Content-Length: $contentLength")
            }

            val buffer = ByteBuffer.allocateDirect(contentLength.toInt())
            val inputStream = response.body?.byteStream() ?: throw Exception("Null response body")

            val tempArray = ByteArray(8192)
            var totalRead = 0
            while (totalRead < contentLength) {
                val toRead = minOf(tempArray.size, contentLength.toInt() - totalRead)
                val bytesRead = inputStream.read(tempArray, 0, toRead)
                if (bytesRead == -1) break
                buffer.put(tempArray, 0, bytesRead)
                totalRead += bytesRead
            }

            inputStream.close()
            buffer
        }
    }

    fun countKs(buffer: ByteBuffer): Int {
        var count = 0
        val readOnlyBuffer = buffer.asReadOnlyBuffer()
        readOnlyBuffer.position(0)
        val limit = readOnlyBuffer.limit()
        repeat(limit) {
            if (readOnlyBuffer.get().toChar() == 'k') count++
        }
        return count
    }
}


/*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import no.SOL.ui.font.FontScaleViewModel
import no.SOL.ui.language.LanguageUtils
import no.SOL.ui.navigation.AppScaffold
import no.SOL.ui.navigation.AppScaffoldController
import no.SOL.ui.navigation.Nav
import no.SOL.ui.onboarding.OnboardingScreen
import no.SOL.ui.onboarding.OnboardingUtils
import no.SOL.ui.reusables.ReplaceVowelsUI
import no.SOL.ui.theme.SOLTheme
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets


class MainActivity : ComponentActivity() {
    companion object {
        init {
            System.loadLibrary("SOL")
        }
    }
    //external fun replaceVowelsWithK(buffer: ByteBuffer, length: Int)

    private val onboardingUtils by lazy { OnboardingUtils(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val languageCode = LanguageUtils.getSavedLanguage(this) ?: "en"
        LanguageUtils.setLanguage(this, languageCode)
        installSplashScreen()
//        onboardingUtils.resetAllOnboardingStates() //for testing
        enableEdgeToEdge()
        setContent {
            SOLTheme {
                /*ReplaceVowelsUI { input ->
                    val bytes = input.toByteArray(StandardCharsets.UTF_8)
                    val directBuffer = ByteBuffer.allocateDirect(bytes.size)
                    directBuffer.put(bytes)
                    directBuffer.rewind()

                    replaceVowelsWithK(directBuffer, bytes.size)

                    val resultBytes = ByteArray(bytes.size)
                    directBuffer.get(resultBytes)
                    String(resultBytes, StandardCharsets.UTF_8)
                }*/
                if (onboardingUtils.isOnboardingCompleted()) {
                    App()
                } else {
                    ShowOnboardingScreen()
                }
            }
        }
    }


    @Composable
    fun App() {
        val navController = rememberNavController()
        val fontScaleViewModel: FontScaleViewModel = viewModel()

        val context = LocalContext.current
        val appScaffoldController = remember { AppScaffoldController(context) }

        val systemFontScale = LocalDensity.current.fontScale
        val effectiveFontScale = systemFontScale * fontScaleViewModel.fontScale.floatValue

        SOLTheme(
            fontScale = effectiveFontScale
        ) {
            AppScaffold(
                navController = navController,
                controller = appScaffoldController,
                fontScaleViewModel = fontScaleViewModel,
            ) { padding ->
                Nav(
                    navController = navController,
                    appScaffoldController = appScaffoldController,
                    contentPadding = padding
                )
            }
        }
    }

    @Composable
    fun ShowOnboardingScreen() {
        val scope = rememberCoroutineScope()
        OnboardingScreen {
            onboardingUtils.setOnboardingCompleted()
            scope.launch {
                setContent {
                    App()
                }
            }
        }
    }
}

*/