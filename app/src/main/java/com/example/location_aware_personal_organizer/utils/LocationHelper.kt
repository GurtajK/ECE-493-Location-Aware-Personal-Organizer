package com.example.location_aware_personal_organizer.utils

import android.content.Context
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
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

        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(query)
            .setTypeFilter(TypeFilter.ESTABLISHMENT) // Restrict to known places
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions.map { it.getFullText(null).toString() }
                onResult(predictions)
            }
            .addOnFailureListener {
                onResult(emptyList()) // Return empty list if API call fails
            }
    }
}
