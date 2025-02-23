package com.example.location_aware_personal_organizer.services

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Authorization private constructor() {
    companion object {
        @Volatile
        private var instance: Authorization? = null;

        fun getInstance() : Authorization {
            if (instance == null)
                instance = Authorization();
            return instance!!;
        }

        private val auth: FirebaseAuth = Firebase.auth;
        private val db: FirebaseFirestore by lazy { Firebase.firestore }

        fun register(username: String, email: String, password: String, confirmPassword: String, successCallback: () -> Unit) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && auth.currentUser != null) {
                        val userdata = hashMapOf("email" to email);
                        db.collection("users")
                            .document(username)
                            .set(userdata)
                            .addOnSuccessListener {
                                successCallback();
                                Log.d("Regitser", "User created successfully");
                            }
                            .addOnFailureListener { e ->
                                Log.w("Register", "User creation failed", e);
                            };
                    } else {
                        Log.w("Register", "User creation failed", task.exception);
                    }
                };
        }

        fun login(username: String, password: String, successCallback: () -> Unit) {
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
                                    Log.w("Login", "User login failed", task.exception);
                                }
                            }
                    }
                }
        }

        fun validate() {

        }
    }
}