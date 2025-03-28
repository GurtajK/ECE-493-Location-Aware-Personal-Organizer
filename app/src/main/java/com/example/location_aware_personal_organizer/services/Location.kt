package com.example.location_aware_personal_organizer.services

import com.google.android.gms.location.FusedLocationProviderClient
import kotlin.properties.Delegates

class Location private constructor() {
    companion object {
        @Volatile
        private var instance: Location? = null

        fun getInstance() : Location {
            if (instance == null)
                instance = Location()
            return instance!!
        }

        lateinit var fusedLocationProviderClient: FusedLocationProviderClient
        var latitude by Delegates.notNull<Double>()
        var longitude by Delegates.notNull<Double>()
        var locationInitialized = false

        fun setLocation(lat: Double, long: Double) {
            latitude = lat
            longitude = long
            locationInitialized = true
        }
    }
}