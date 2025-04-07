package com.example.location_aware_personal_organizer.util_test

import org.junit.Assert.*
import org.junit.Test

class AuthorizationLogicUnitTest {

    // original Pattern only exist in android runtime, this is equuivalent in JVM
    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        return emailRegex.matches(email)
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 12 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { !it.isLetterOrDigit() }
    }

    @Test
    fun `valid email returns true`() {
        assertTrue(isValidEmail("test@example.com"))
    }

    @Test
    fun `invalid email returns false`() {
        assertFalse(isValidEmail("invalid-email"))
    }

    @Test
    fun `valid password returns true`() {
        assertTrue(isValidPassword("Valid@Password123"))
    }

    @Test
    fun `short password returns false`() {
        assertFalse(isValidPassword("Sh0rt@"))
    }

    @Test
    fun `password with no uppercase returns false`() {
        assertFalse(isValidPassword("lowercase@1234"))
    }

    @Test
    fun `password with no lowercase returns false`() {
        assertFalse(isValidPassword("UPPERCASE@1234"))
    }

    @Test
    fun `password with no special character returns false`() {
        assertFalse(isValidPassword("NoSpecialChar123"))
    }
}
