package com.example.location_aware_personal_organizer.util_test

import com.example.location_aware_personal_organizer.data.LocationSuggestion
import com.example.location_aware_personal_organizer.utils.LocationHelper
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.reflect.full.companionObjectInstance


class LocationHelperUnitTest {

    @Before
    fun setUp() {
        LocationHelper.initialized = false
    }

    @Test
    fun `setLocation updates latitude and longitude correctly`() {
        val lat = 10.123
        val lon = -20.456

        val companion = LocationHelper::class.companionObjectInstance!!
        val setLocationMethod = companion::class.java.getDeclaredMethod("setLocation", Double::class.java, Double::class.java)
        setLocationMethod.isAccessible = true
        setLocationMethod.invoke(companion, lat, lon)

        assertEquals(lat, LocationHelper.latitude, 0.0001)
        assertEquals(lon, LocationHelper.longitude, 0.0001)
        assertTrue(LocationHelper.initialized)
    }

    @Test
    fun `initialized is false before setting location`() {
        assertFalse(LocationHelper.initialized)
    }


    @Test
    fun `fetchLocationSuggestions with empty query returns empty list`(): Unit = runBlocking {
        var result: List<LocationSuggestion>? = null

        // Mock PlacesClient but it won't be used for empty query
        val mockedPlacesClient = mock<PlacesClient>()

        LocationHelper.fetchLocationSuggestions("", mockedPlacesClient) {
            result = it
        }

        assertNotNull(result)
        assertTrue(result!!.isEmpty())
    }

}
