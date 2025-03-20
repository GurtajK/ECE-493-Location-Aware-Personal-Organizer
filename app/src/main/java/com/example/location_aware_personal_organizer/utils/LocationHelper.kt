package com.example.location_aware_personal_organizer.utils

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
    onResult: (List<String>) -> Unit
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
                val predictions =
                    response.autocompletePredictions.map { it.getFullText(null).toString() }
                onResult(predictions)
            }
            .addOnFailureListener {
                it.printStackTrace()
                onResult(emptyList())
            }
    }
}
