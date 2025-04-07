package com.example.location_aware_personal_organizer.util_test

import com.example.location_aware_personal_organizer.data.Task
import com.example.location_aware_personal_organizer.services.PriorityService
import com.example.location_aware_personal_organizer.utils.LocationHelper
import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PriorityServiceUnitTest {

    @Before
    fun setUp() {
        LocationHelper.latitude = 43.65107
        LocationHelper.longitude = -79.347015
        LocationHelper.initialized = true
    }

    @Test
    fun `haversineDistance returns 0 for same location`() {
        val task = Task(
            location = com.google.firebase.firestore.GeoPoint(43.65107, -79.347015)
        )

        val method = PriorityService.Companion::class.java
            .getDeclaredMethod("haversineDistance", Task::class.java)
        method.isAccessible = true
        val distance = method.invoke(PriorityService, task) as Double

        assertEquals(0.0, distance, 0.0001)
    }

    @Test
    fun `prioritizeTasks sets correct priority values`() {
        val task = Task(
            deadline = Timestamp.now(),
            location = com.google.firebase.firestore.GeoPoint(43.7000, -79.4000),
            complete = false
        )
        val tasks = listOf(task)

        PriorityService.prioritizeTasks(tasks)

        assertTrue(task.priority > 0)
        assertTrue(task.timePriority >= 0)
        assertTrue(task.distancePriority >= 0)
    }
}
