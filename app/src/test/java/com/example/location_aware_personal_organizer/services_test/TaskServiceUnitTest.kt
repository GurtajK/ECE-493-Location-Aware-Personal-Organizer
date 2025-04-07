package com.example.location_aware_personal_organizer.util_test

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class TaskServiceLogicTest {

    private fun calculateNotifyAtMillis(deadline: Date, notifyMinutes: Int): Long {
        val calendar = Calendar.getInstance().apply {
            time = deadline
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MINUTE, -notifyMinutes)
        }
        return calendar.timeInMillis
    }

    @Test
    fun `notify time is calculated correctly`() {
        val deadline = Calendar.getInstance().apply {
            set(2025, Calendar.APRIL, 7, 14, 30, 45)
            set(Calendar.MILLISECOND, 999)
        }.time

        val notifyMinutes = 5
        val result = calculateNotifyAtMillis(deadline, notifyMinutes)

        val expected = Calendar.getInstance().apply {
            set(2025, Calendar.APRIL, 7, 14, 25, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        assertEquals(expected, result)
    }
}
