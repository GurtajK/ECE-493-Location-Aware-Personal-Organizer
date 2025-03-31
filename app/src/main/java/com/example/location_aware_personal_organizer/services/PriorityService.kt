package com.example.location_aware_personal_organizer.services

import android.util.Log
import com.example.location_aware_personal_organizer.data.Task
import com.example.location_aware_personal_organizer.utils.LocationHelper
import com.google.firebase.Timestamp
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class PriorityService private constructor() {
    companion object {
        @Volatile
        private var instance: PriorityService? = null
        // used for the Haversine formula
        private const val EARTH_RADIUS_KM = 6371.0
        private const val distance_scale = 10f  // 10km for higher end of the distance spectrum
        private const val time_scale = 7f*24f*60f*60f // 1 week for higher end of the time spectrum
        private const val dt_ratio = time_scale / distance_scale   // multiply all distances by this
        private const val priority_scale = time_scale + distance_scale*dt_ratio    // approximate higher end of the spectrum for priorities
        const val threshold = priority_scale*0.2f   // threshold for sending notifications (one fourth of the priority scale)

        // alternatively we might want to exponentially scale the priority value as location and time increases
        // i.e. (e^0.2x - 1) or something, this way for smaller distances the priority will be more similar but as distance
        // gets large it will have a more drastic effect on increasing priority
        // We would need to play around with the threshold value if that was the case

        fun getInstance() : PriorityService {
            if (instance == null)
                instance = PriorityService()
            return instance!!
        }

        fun prioritizeTasks(tasks: List<Task>) {
            val currentTimestamp = Timestamp.now()

            for (task in tasks) {
                task.distance = haversineDistance(task)
                task.time_priority = (task.deadline!!.seconds - currentTimestamp.seconds).toDouble()
                task.distance_priority = task.distance* dt_ratio
                task.priority = task.time_priority + task.distance_priority
            }

            val minprio = tasks.minOf { it.priority }
            val maxprio = tasks.maxOf { it.priority }

            Log.d("PriorityService", "Min priority: $minprio, Max priority: $maxprio")
        }

        private fun haversineDistance(it: Task) : Double {
            // Calculate the Haversine distance for the km between the current location and the task location
            if (it.location == null) {
                return Double.MAX_VALUE
            }
            val lat1 = Math.toRadians(LocationHelper.latitude)
            val lon1 = Math.toRadians(LocationHelper.longitude)
            val lat2 = Math.toRadians(it.location.latitude)
            val lon2 = Math.toRadians(it.location.longitude)
            val lat = lat2 - lat1
            val lon = lon2 - lon1
            val distance = 2 * EARTH_RADIUS_KM *
                asin(
                    sqrt(
                    sin(lat/2).pow(2)
                        + cos(lat1)*cos(lat2)*sin(lon/2).pow(2)
                    )
                )
            return distance
        }
    }
}