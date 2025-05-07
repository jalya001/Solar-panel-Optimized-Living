package no.solcellepanelerApp.ui.onboarding

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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

import no.solcellepanelerApp.model.onboarding.OnBoardModel
import no.solcellepanelerApp.ui.language.LangSwitch
import no.solcellepanelerApp.ui.reusables.ModeCard
import no.solcellepanelerApp.ui.theme.ThemeMode
import no.solcellepanelerApp.ui.theme.ThemeState


@Composable
fun OnboardingGraphUI(onBoardModel: OnBoardModel) {
    val isDark = isSystemInDarkTheme()
    var triggerLocationFetch by remember { mutableStateOf(false) }

//    var region: Region? by remember { mutableStateOf(null) }
//    val (currentLocation, locationGranted) = if (triggerLocationFetch) {
//        rememberLocationWithPermission(
//            triggerRequest = true,
//            onRegionDetermined = { region = it }
//        )
//    } else {
//        Pair(null, false)
//    }

    val imageRes = when (onBoardModel) {
        is OnBoardModel.FirstPage -> if (isDark) R.drawable.onboard_logo_dark else R.drawable.onboard_logo_light
        is OnBoardModel.SecondPage -> R.drawable.baseline_lightbulb_circle_24
        is OnBoardModel.ThirdPage -> R.drawable.school_24px
        is OnBoardModel.FourthPage -> R.drawable.baseline_my_location_24
        is OnBoardModel.FifthPage -> R.drawable.palette_24px
    }

    val descriptionText: AnnotatedString = when (onBoardModel) {
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

        is OnBoardModel.FifthPage -> buildAnnotatedString {

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

        if (onBoardModel !is OnBoardModel.FifthPage) {
            Spacer(modifier = Modifier.size(50.dp))
        }

        Text(
            text = stringResource(onBoardModel.titleRes),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.tertiary
        )

        if (onBoardModel !is OnBoardModel.FifthPage) {
            Spacer(modifier = Modifier.size(15.dp))
        }

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

        if (onBoardModel is OnBoardModel.FourthPage) {
            val context = LocalContext.current
            val locationGranted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            var showDialog by remember { mutableStateOf(false) }

            if (showDialog) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(stringResource(R.string.location_perm_title), style = MaterialTheme.typography.bodyLarge) },
                    text = { Text(stringResource(R.string.location_perm_content), style = MaterialTheme.typography.bodyLarge) },
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
                            Text(stringResource(R.string.settings), style = MaterialTheme.typography.bodyLarge))
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text(stringResource(R.string.close), style = MaterialTheme.typography.bodyLarge)
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
                Text(
                    if (locationGranted) stringResource(R.string.change_location_settings) else stringResource(
                        R.string.grant_location_access
                    ), style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        if (onBoardModel is OnBoardModel.FifthPage) {
            var followSystem by remember { mutableStateOf(ThemeState.themeMode == ThemeMode.SYSTEM) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ModeCard(
                    label = stringResource(id = R.string.light_mode),
                    iconRes = R.drawable.light_mode_24px,
//                    selected = ThemeState.themeMode == ThemeMode.LIGHT && !followSystem,
                    onClick = {
                        followSystem = false
                        ThemeState.themeMode = ThemeMode.LIGHT
                    }
                )

                ModeCard(
                    label = stringResource(id = R.string.dark_mode),
                    iconRes = R.drawable.dark_mode_24px,
//                    selected = ThemeState.themeMode == ThemeMode.DARK && !followSystem,
                    onClick = {
                        followSystem = false
                        ThemeState.themeMode = ThemeMode.DARK
                    }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = followSystem,
                    onCheckedChange = { checked ->
                        followSystem = checked
                        ThemeState.themeMode = if (checked) ThemeMode.SYSTEM
                        else ThemeMode.LIGHT
                    }
                )
                Text(
                    stringResource(id = R.string.follow_system),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LangSwitch()

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                stringResource(id = R.string.onboard_desc_5),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(60.dp)
        )
    }
}
