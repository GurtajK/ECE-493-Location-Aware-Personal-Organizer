package com.example.location_aware_personal_organizer.utils

import com.example.location_aware_personal_organizer.data.LocationSuggestion
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

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

        fun setLocation(lat: Double, long: Double) {
            latitude = lat
            longitude = long
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
    }
}