package com.example.location_aware_personal_organizer.utils

import com.example.location_aware_personal_organizer.data.LocationSuggestion
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun fetchLocationSuggestions(
    query: String,
    placesClient: PlacesClient,
    latitude: Float,
    longitude: Float,
    onResult: (List<LocationSuggestion>) -> Unit
) {
    withContext(Dispatchers.IO) { // Run this on a background thread
        if (query.isEmpty()) {
            onResult(emptyList())
            return@withContext
        }

        val latitudeDouble = latitude.toDouble()
        val longitudeDouble = longitude.toDouble()


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
