package no.solcellepanelerApp.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class LocationService(private val activity: Activity) {
    private val fusedLocationClient: FusedLocationProviderClient =
        activity.let { LocationServices.getFusedLocationProviderClient(it) }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        //Check for necessary permissions
        val hasPermission = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return if (hasPermission) {
            try {
                fusedLocationClient.lastLocation.await()
            } catch (e: Exception) {
                Log.e("LocationService", "Error getting location", e)
                null
            }
        } else {
            null
        }
    }
}