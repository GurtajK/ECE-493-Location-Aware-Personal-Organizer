package com.example.location_aware_personal_organizer.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class Task(
    @DocumentId val id: String? = null, // Firestore will generate the ID
    val title: String = "",
    val description: String = "",
    val deadline: Timestamp? = null,
    val location: GeoPoint? = null,
    val locationName: String = "",
    val notify: Int = 0,
    var complete: Boolean = false,
    val user: String = ""
)