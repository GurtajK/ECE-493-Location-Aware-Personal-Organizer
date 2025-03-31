package com.example.location_aware_personal_organizer.services

import android.util.Log
import com.example.location_aware_personal_organizer.data.Task
import com.example.location_aware_personal_organizer.utils.LocationHelper
import com.google.firebase.Timestamp
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class PriorityService private constructor() {
    companion object {
        @Volatile
        private var instance: PriorityService? = null
        // used for the Haversine formula
        private const val EARTH_RADIUS_KM = 6371.0
        private const val DISTANCE_SCALE = 10f  // 10km for higher end of the distance spectrum
        private const val TIME_SCALE = 7f*24f*60f*60f // 1 week for higher end of the time spectrum
        private const val DT_RATIO = TIME_SCALE / DISTANCE_SCALE   // multiply all distances by this
        private const val PRIORITY_SCALE = TIME_SCALE + DISTANCE_SCALE*DT_RATIO    // approximate higher end of the spectrum for priorities
        private const val INDIVIDUAL_THRESHOLD = PRIORITY_SCALE*0.05f   // threshold for sending notifications (one fourth of the priority scale)

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
                task.timePriority = (task.deadline!!.seconds - currentTimestamp.seconds).toDouble()
                task.distancePriority = task.distance* DT_RATIO
                task.priority =
                    if (task.timePriority < INDIVIDUAL_THRESHOLD || task.distancePriority < INDIVIDUAL_THRESHOLD)
                        min(task.timePriority, task.distancePriority) + 0.25* max(task.timePriority, task.distancePriority)
                    else task.timePriority + task.distancePriority
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