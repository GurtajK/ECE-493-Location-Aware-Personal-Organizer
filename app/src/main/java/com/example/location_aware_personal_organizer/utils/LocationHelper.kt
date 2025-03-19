package com.example.location_aware_personal_organizer.utils

import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun fetchLocationSuggestions(
    query: String,
    placesClient: PlacesClient,
    onResult: (List<String>) -> Unit
) {
    withContext(Dispatchers.IO) { // Run this on a background thread
        if (query.isEmpty()) {
            onResult(emptyList())
            return@withContext
        }

        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
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
