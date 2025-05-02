package no.solcellepanelerApp.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.solcellepanelerApp.MainActivity
import no.solcellepanelerApp.data.location.LocationService
import no.solcellepanelerApp.model.electricity.Region

// Funksjon for å sjekke og lagre tillatelse
fun isLocationPermissionGranted(context: Context): Boolean {
    val sharedPreferences =
        context.getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("LocationPermissionGranted", false)
}

fun setLocationPermissionGranted(context: Context, granted: Boolean) {
    val sharedPreferences =
        context.getSharedPreferences("LocationPreferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("LocationPermissionGranted", granted)
    editor.apply()
}

// Funksjon for å be om lokasjonstillatelse og hente region
@Composable
fun RequestLocationPermission(
    onLocationFetched: (Region) -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? Activity
    var selectedRegion by rememberSaveable { mutableStateOf<Region?>(null) }

    // Launcher for å be om tillatelse, skal kun opprettes én gang
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        setLocationPermissionGranted(context, isGranted)
        if (isGranted && activity != null) {
            fetchLocation(context, activity, onLocationFetched)
        } else {
            selectedRegion = Region.OSLO // Fallback hvis tillatelsen blir nektet
            onLocationFetched(selectedRegion ?: Region.OSLO)
        }
    }

    // Vi skal be om tillatelse hvis den ikke er gitt
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Spør om tillatelse hvis ikke allerede gitt
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            // Hvis tillatelsen er gitt, hent lokasjon
            fetchLocation(context, activity, onLocationFetched)
        }
    }
}


private fun fetchLocation(
    context: Context,
    activity: Activity?,
    onLocationFetched: (Region) -> Unit,
) {
    val locationService = LocationService(activity!!)
    try {
        CoroutineScope(Dispatchers.Main).launch {
            val location = locationService.getCurrentLocation()
            val region = location?.let { mapLocationToRegion(it) }
            if (region != null) {
                onLocationFetched(region)
            } else {
                // Hvis ingen lokasjon finnes, fall tilbake til Oslo
                onLocationFetched(Region.OSLO)
            }
        }
    } catch (e: Exception) {
        Log.e("LocationPermission", "Feil ved henting av lokasjon", e)
        // Fallback
        onLocationFetched(Region.OSLO)
    }
}


// Funksjon for å mappe lokasjon til region
fun mapLocationToRegion(location: Location): Region {
    val lat = location.latitude
    val lon = location.longitude

    return when {
        // Østlandet / Oslo (NO1)
        lat in 59.5..61.5 && lon in 9.0..12.5 -> Region.OSLO
        // Sørlandet / Kristiansand (NO2)
        lat in 57.5..59.5 && lon in 6.0..9.5 -> Region.KRISTIANSAND
        // Midt-Norge / Trondheim (NO3)
        lat in 62.0..64.5 && lon in 9.0..12.0 -> Region.TRONDHEIM
        // Nord-Norge / Tromsø (NO4)
        lat in 68.0..70.5 && lon in 17.0..20.5 -> Region.TROMSO
        // Vestlandet / Bergen (NO5)
        lat in 60.0..61.5 && lon in 4.5..6.5 -> Region.BERGEN
        // Fallback hvis vi ikke finner match
        else -> Region.OSLO
    }
}

suspend fun fetchCoordinates(
    context: Context,
    activity: Activity?,

    ): Location? {
    val locationService = LocationService(activity!!)
    return try {
        locationService.getCurrentLocation()

    } catch (e: Exception) {
        Log.e("LocationPermission", "Feil ved henting av lokasjon", e)
        null
    }
}

@SuppressLint("MissingPermission")
@Composable
fun RememberLocationWithPermission(
    triggerRequest: Boolean,
    onRegionDetermined: (Region?) -> Unit,
): Pair<Location?, Boolean> {
    val context = LocalContext.current
    val activity = context as? MainActivity

    var locationPermissionGranted by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var permissionDeniedPermanently by remember { mutableStateOf(false) }

    val permissionCheck =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
        activity!!,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Check permission on recomposition
    LaunchedEffect(Unit) {
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else if (!showRationale && permissionCheck != PackageManager.PERMISSION_GRANTED) {
            permissionDeniedPermanently = true
        }
    }

    // Request location permission and fetch region
//    if (triggerRequest && !locationPermissionGranted && !permissionDeniedPermanently) {  var ikke detet som var problemet
    if (triggerRequest && !locationPermissionGranted) {
        RequestLocationPermission { region ->
            onRegionDetermined(region)
            locationPermissionGranted = true
        }
    }

    // Once permission is granted, fetch coordinates
    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted && activity != null) {
            val location = fetchCoordinates(context, activity)
            currentLocation = location
        }
    }

    // Show alert dialog if permission is denied permanently
    if (permissionDeniedPermanently) {
        LaunchedEffect(Unit) {
            AlertDialog.Builder(context)
                .setTitle("Location Permission Needed")
                .setMessage("Please enable location permission in settings to use this feature.")
                .setPositiveButton("Go to Settings") { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    return Pair(currentLocation, locationPermissionGranted)
}