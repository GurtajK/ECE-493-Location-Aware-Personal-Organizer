package com.example.location_aware_personal_organizer.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

// FR 28 Save.Task
data class Task(
    @DocumentId val id: String? = null, // Firestore will generate the ID
    val title: String = "",
    val description: String = "",
    val deadline: Timestamp? = null,
    val location: GeoPoint? = null,
    val locationName: String = "",
    val notify: Int = 0,
    var complete: Boolean = false,
    val user: String = "",

    // default to not being prioritized (i.e. above the threshold)
    var distancePriority : Double = Double.MAX_VALUE,
    var timePriority : Double = Double.MAX_VALUE,
    var priority: Double = Double.MAX_VALUE,
    var distance: Double = Double.MAX_VALUE,
)