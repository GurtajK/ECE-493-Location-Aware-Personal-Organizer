package com.example.location_aware_personal_organizer.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Task(
    @DocumentId val id: String? = null, // Firestore will generate the ID
    val title: String = "",
    val description: String = "",
    val deadline: Timestamp? = null,
    val location: String = "",
    val notify: Int = 0,
    val complete: Boolean = false,
    val user: String = ""
)