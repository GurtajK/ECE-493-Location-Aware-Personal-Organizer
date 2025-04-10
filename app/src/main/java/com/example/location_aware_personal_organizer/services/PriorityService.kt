package com.example.location_aware_personal_organizer.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.work.Configuration
import com.example.location_aware_personal_organizer.data.Task
import com.example.location_aware_personal_organizer.utils.LocationHelper
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class PriorityService : JobService() {
    init {
        val builder: Configuration.Builder = Configuration.Builder()
        builder.setJobSchedulerJobIdRange(0, 1000)
    }

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

        fun getInstance() : PriorityService {
            if (instance == null)
                instance = PriorityService()

            return instance!!
        }

        // FR 68 Prioritization.Heuristic
        fun prioritizeTasks(tasks: List<Task>) {
            if (!LocationHelper.initialized) {
                Log.e("ProxiPlan Location", "LOCATION IS NOT SET")
                return
            }

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
        }

        // FR 68 Prioritization.Heuristic
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

        // FR 55 Notify.Task
        // FR 69 Prioritization.Location
        private suspend fun reprioritizeAndNotify(context: Context) {
            // if location is not initialized
            var tasks = TaskService.getTasksForCurrentUser()

            // always get an updated location
            LocationHelper.updateCurrentLocation(context = context, successCallback = {
                prioritizeTasks(tasks)
                tasks = tasks.sortedBy { it.priority } .filter { !it.complete }

                if (tasks.isNotEmpty()) {
                    val title = tasks.first().title
                    val df = DecimalFormat("#0.00");
                    val distance = df.format(tasks.first().distance)
                    Log.d("bg job", "sending notification")

                    val deadline = SimpleDateFormat("MMM d h:mm a", Locale.getDefault()).format(tasks.first().deadline!!.toDate())
                    NotificationHelper(context).sendNotification("Prioritized Task", "$title is due on $deadline\n$distance km away")
                }
            })
        }
    }

    // FR 69 Prioritization.Location
    override fun onStartJob(p0: JobParameters?): Boolean {
        getInstance()
        // begin background process updates
        val context = this
        LocationHelper.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        Log.d("bg job", "background reprioritize started")
        CoroutineScope(Job() + Dispatchers.Main).launch {
            reprioritizeAndNotify(context)
        }
        return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }
}