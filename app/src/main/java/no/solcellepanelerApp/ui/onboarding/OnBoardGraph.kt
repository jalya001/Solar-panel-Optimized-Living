package no.solcellepanelerApp.ui.onboarding

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import no.solcellepanelerApp.R
import no.solcellepanelerApp.model.electricity.Region
import no.solcellepanelerApp.util.RememberLocationWithPermission

@Composable
fun OnboardingGraphUI(OnBoardModel: OnBoardModel) {
    val isDark = isSystemInDarkTheme()
    var triggerLocationFetch by remember { mutableStateOf(false) }

    var region: Region? by remember { mutableStateOf(null) }
    val (currentLocation, locationGranted) = if (triggerLocationFetch) {
        RememberLocationWithPermission(
            triggerRequest = true,
            onRegionDetermined = { region = it }
        )
    } else {
        Pair(null, false)
    }

    val imageRes = when (OnBoardModel) {
        is OnBoardModel.FirstPage -> if (isDark) R.drawable.onboard_logo_dark else R.drawable.onboard_logo_light
        is OnBoardModel.SecondPage -> R.drawable.home_24px
        is OnBoardModel.ThirdPage -> R.drawable.school_24px
        is OnBoardModel.FourthPage -> R.drawable.baseline_my_location_24
    }

    val descriptionText: AnnotatedString = when (OnBoardModel) {
        is OnBoardModel.FirstPage -> buildAnnotatedString {
            withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {
                append(stringResource(id = R.string.onboard_desc_1))
            }
        }

        is OnBoardModel.SecondPage -> buildAnnotatedString {
            withStyle(style = MaterialTheme.typography.titleLarge.toSpanStyle()) {
                append(stringResource(id = R.string.onboard_highlight_1))
            }
            append("\n")
            withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {
                append(stringResource(id = R.string.onboard_desc_2_1))
            }
            append("\n\n")

            withStyle(style = MaterialTheme.typography.titleLarge.toSpanStyle()) {
                append(stringResource(id = R.string.onboard_highlight_2))
            }
            append("\n")
            withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {
                append(stringResource(id = R.string.onboard_desc_2_2))
            }
            append("\n\n")

            withStyle(style = MaterialTheme.typography.titleLarge.toSpanStyle()) {
                append(stringResource(id = R.string.onboard_highlight_3))
            }
            append("\n")
            withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {

                append(stringResource(id = R.string.onboard_desc_2_3))
            }
            append("\n\n")

            withStyle(style = MaterialTheme.typography.titleLarge.toSpanStyle()) {
                append(stringResource(id = R.string.onboard_highlight_4))
            }
            append("\n")
            withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {
                append(stringResource(id = R.string.onboard_desc_2_4))
            }
        }

        is OnBoardModel.ThirdPage -> buildAnnotatedString {
            withStyle(style = MaterialTheme.typography.titleLarge.toSpanStyle()) {
                append(stringResource(id = R.string.onboard_how_1))
            }
            append("\n")
            withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {

                append(stringResource(id = R.string.onboard_how_desc_1))
            }
            append("\n\n")

            withStyle(style = MaterialTheme.typography.titleLarge.toSpanStyle()) {
                append(stringResource(id = R.string.onboard_how_2))
            }
            append("\n")
            withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {

                append(stringResource(id = R.string.onboard_how_desc_2))
            }
            append("\n\n")

            withStyle(style = MaterialTheme.typography.titleLarge.toSpanStyle()) {
                append(stringResource(id = R.string.onboard_how_3))
            }
            append("\n")
            withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {

                append(stringResource(id = R.string.onboard_how_desc_3))
            }
            append("\n\n")

            withStyle(style = MaterialTheme.typography.titleLarge.toSpanStyle()) {
                append(stringResource(id = R.string.onboard_how_4))
            }
            append("\n")
            withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {

                append(stringResource(id = R.string.onboard_how_desc_4))
            }
        }

        is OnBoardModel.FourthPage -> buildAnnotatedString {
            withStyle(style = MaterialTheme.typography.bodyLarge.toSpanStyle()) {

                append(stringResource(id = R.string.onboard_desc_4))
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        val logo = if (isDark) R.drawable.onboard_logo_dark else R.drawable.onboard_logo_light

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier =
                if (imageRes != logo) Modifier.size(50.dp) else Modifier.size(200.dp),
            alignment = Alignment.Center,
            colorFilter = if (imageRes != logo) ColorFilter.tint(MaterialTheme.colorScheme.primary) else null
        )

        Spacer(modifier = Modifier.size(50.dp))

        Text(
            text = stringResource(OnBoardModel.titleRes),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.tertiary
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(15.dp)
        )

        Text(
            text = descriptionText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp, 0.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(10.dp)
        )

        if (OnBoardModel is OnBoardModel.FourthPage) {
            val context = LocalContext.current
            val locationGranted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            var showDialog by remember { mutableStateOf(false) }

            if (showDialog) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Location Permission Already Granted") },
                    text = { Text("If you want to change location permissions, go to settings.") },
                    confirmButton = {
                        Button(onClick = {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                            context.startActivity(intent)
                            showDialog = false
                        }) {
                            Text("Go to Settings")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            Button(onClick = {
                if (locationGranted) {
                    showDialog = true
                } else {
                    triggerLocationFetch = true
                }
            }) {
                Text(if (locationGranted) "Change Location Settings" else "Grant Location Access")
            }
        }



        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(60.dp)
        )
    }
}
