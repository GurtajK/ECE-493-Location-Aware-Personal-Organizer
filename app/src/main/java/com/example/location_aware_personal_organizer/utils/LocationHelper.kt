package com.example.location_aware_personal_organizer.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.location_aware_personal_organizer.data.LocationSuggestion
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LastLocationRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

// FR 33 Task.Create
// FR 68 Prioritization.Heuristic
// FR 69 Prioritization.Location
class LocationHelper private constructor() {
    companion object {
        @Volatile
        private var instance: LocationHelper? = null

        fun getInstance() : LocationHelper {
            if (instance == null)
                instance = LocationHelper()
            return instance!!
        }

        lateinit var fusedLocationProviderClient: FusedLocationProviderClient
        var latitude by Delegates.notNull<Double>()
        var longitude by Delegates.notNull<Double>()
        var initialized = false

        private fun setLocation(lat: Double, long: Double) {
            latitude = lat
            longitude = long
            initialized = true
        }

        suspend fun fetchLocationSuggestions(
            query: String,
            placesClient: PlacesClient,
            onResult: (List<LocationSuggestion>) -> Unit
        ) {
            withContext(Dispatchers.IO) { // Run this on a background thread
                if (query.isEmpty()) {
                    onResult(emptyList())
                    return@withContext
                }

                val latitudeDouble = LocationHelper.latitude.toDouble()
                val longitudeDouble = LocationHelper.longitude.toDouble()


                val center = LatLng(latitudeDouble, longitudeDouble)
                val circle = CircularBounds.newInstance(center,  /* radius = */50000.0)

                val request = FindAutocompletePredictionsRequest.builder()
                    .setQuery(query)
                    .setLocationRestriction(circle)
                    .build()

                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response ->
                        val predictions = response.autocompletePredictions.map {
                            LocationSuggestion (
                                name = it.getFullText(null).toString(),
                                placeId = it.placeId
                            )
                        }
                        onResult(predictions)

                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        onResult(emptyList())
                    }
            }
        }

        fun updateCurrentLocation(activity: Activity? = null, context: Context? = null, successCallback : () -> Unit = {} )
        {
            val context = activity ?: context!!
            Log.d("bg location", context.toString())
            if(checkPermissions(context))
            {
                Log.d("bg location", "check perm true")

                if(isLocationEnabled(context))
                {
                    Log.d("bg location", "check loc true")
                    // final latitude and longitude code here
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d("bg location", "check self perm false")
                        if (activity != null)
                            requestPermission(activity)
                        return
                    }

                    var cts : CancellationTokenSource? = null
                    val listener = { task : Task<Location> ->
                        Log.d("bg location", if (task.isSuccessful) "task success" else "task fail")
                        if (task.isSuccessful) {
                            val location: Location? = task.result
                            if (location == null) {
                                Log.d("bg location", "null location")
                                Toast.makeText(context, "Null Received", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                if (cts != null)
                                    cts!!.cancel()
                                Log.d("bg location", "get success")
                                //Toast.makeText(context, "Get Success", Toast.LENGTH_SHORT).show()
                                setLocation(location.latitude, location.longitude)
                                successCallback()
                            }
                        }
                    }

                    if (activity != null) {
                        Log.d("bg location", "starting fused listener w act")
                        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).addOnCompleteListener(activity, listener)
                    } else {
                        Log.d("bg location", "starting fused listener w js")
                        cts = CancellationTokenSource()
                        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token).addOnCompleteListener(listener)
                        fusedLocationProviderClient.lastLocation.addOnCompleteListener(listener)
                    }
                }
                else
                {
                    Log.d("bg location", "check loc false")
                    // setting open here
                    if (activity != null) {
                        Toast.makeText(context, "Turn on location", Toast.LENGTH_SHORT).show()
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        activity.startActivity(intent)
                    } else {
                        // background service restarted but has no location
                        Toast.makeText(context, "LAPO failed to turn on background location", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else
            {
                Log.d("bg location", "check perm false")
                // request permission here
                if (activity != null)
                    requestPermission(activity)
                else
                    // background service restarted but has no location
                    Toast.makeText(context, "LAPO failed to turn on background location", Toast.LENGTH_SHORT).show()
            }
        }

        private fun isLocationEnabled(context: Context): Boolean{
            val locationManager: LocationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

        private fun requestPermission(activity: Activity) {
            ActivityCompat.requestPermissions(
                    activity, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_LOCATION
            )
        }

        const val PERMISSION_REQUEST_ACCESS_LOCATION = 100

        fun checkPermissions(context: Context): Boolean {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }
}