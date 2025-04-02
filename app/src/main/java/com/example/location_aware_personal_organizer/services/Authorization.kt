package com.example.location_aware_personal_organizer.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.location_aware_personal_organizer.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Authorization private constructor() {
    companion object {
        @Volatile
        private var instance: Authorization? = null

        fun getInstance() : Authorization {
            if (instance == null)
                instance = Authorization()
            return instance!!
        }

        private val auth: FirebaseAuth = Firebase.auth
        private val db: FirebaseFirestore by lazy { Firebase.firestore }

        fun register(
            username: String,
            email: String,
            password: String,
            confirmPassword: String,
            successCallback: () -> Unit,
            context: Context
        ) {
            if (isValidEmail(email) && isValidPassword(password) && password == confirmPassword) {
                db.collection("users").get().addOnCompleteListener { dbtask ->
                    if (dbtask.isSuccessful) {
                        if (dbtask.result.documents.any { it.id == username }) {
                            Toast.makeText(
                                context,
                                R.string.username_exists,
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else if (dbtask.result.documents.any { it.data!!["email"].toString() == email }) {
                            Toast.makeText(
                                context,
                                R.string.email_registered,
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful && auth.currentUser != null) {

                                        val firebaseUser = auth.currentUser

                                        // Set the displayName for the user
                                        val profileUpdates = userProfileChangeRequest {
                                            displayName = username
                                        }
                                        firebaseUser?.updateProfile(profileUpdates)
                                            ?.addOnCompleteListener { profileTask ->
                                                if (profileTask.isSuccessful) {
                                                    Log.d("Register", "DisplayName set to: $username")
                                                } else {
                                                    Log.e("Register", "Failed to set displayName", profileTask.exception)
                                                }
                                            }
                                        // Send email verification
                                        firebaseUser?.sendEmailVerification()
                                            ?.addOnCompleteListener { emailTask ->
                                                if (emailTask.isSuccessful) {
                                                    Toast.makeText(
                                                        context,
                                                        "Verification email sent to ${firebaseUser.email}",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                } else {
                                                    Log.e("Register", "Failed to send verification email", emailTask.exception)
                                                }
                                            }
                                        // Store the user in Firestore
                                        val userdata = hashMapOf("email" to email);
                                        db.collection("users")
                                            .document(username)
                                            .set(userdata)
                                            .addOnSuccessListener {
                                                successCallback();
                                                Toast.makeText(
                                                    context,
                                                    "Registration successful",
                                                    Toast.LENGTH_SHORT,
                                                ).show()
                                                Log.d("Register", "User created successfully")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.w("Register", "User creation failed", e)
                                                Toast.makeText(
                                                    context,
                                                    R.string.generic_error,
                                                    Toast.LENGTH_SHORT,
                                                ).show()
                                            };


                                    } else {
                                        Log.w("Register", "User creation failed", task.exception)
                                        Toast.makeText(
                                            context,
                                            task.exception?.message ?: "",
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                    }
                                };
                        }
                    } else {
                        // username query failed
                        Toast.makeText(
                            context,
                            R.string.generic_error,
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

            } else {
                Toast.makeText(
                    context,
                    R.string.invalid_fields,
                    Toast.LENGTH_SHORT,
                ).show()
            }

        }

        fun login(username: String, password: String, successCallback: () -> Unit, context: Context) {
            db.collection("users").document(username).get()
                .addOnSuccessListener { result ->
                    if (result.exists()) {
                        val email = result.data!!["email"].toString();
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("Login", "User logged in successfully");
                                    successCallback();
                                } else {
                                    Toast.makeText(
                                        context,
                                        R.string.invalid_credentials,
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                    Log.w("Login", "User login failed", task.exception);
                                }
                            }
                    } else {
                        Toast.makeText(
                            context,
                            R.string.invalid_credentials,
                            Toast.LENGTH_SHORT,
                        ).show()
                        Log.w("Login", "User not found");
                    }
                }
        }

        fun logout(successCallback: () -> Unit) : Boolean {
            try {
                auth.signOut()
                Log.d("Logout", "User logged out successfully")
                Log.d("Logout", auth.currentUser.toString())
                successCallback()
                return true
            } catch (e: Exception) {
                Log.w("Logout", "User logout failed", e)
                return false
            }
        }

        fun getUsername() : String {
            return auth.currentUser!!.displayName!!
        }

        fun isValidEmail(email: String) : Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

        fun isValidPassword(password: String) : Boolean {
            return password.length >= 12 && password.any { it.isUpperCase() } && password.any { it.isLowerCase() } && password.any { !it.isLetterOrDigit() };
        }
    }
}