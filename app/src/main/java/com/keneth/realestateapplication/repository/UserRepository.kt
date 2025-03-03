package com.keneth.realestateapplication.repository

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.keneth.realestateapplication.UserPreferences
import com.keneth.realestateapplication.data.User
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {

    // Sign up a new user with email, password, and additional user data
    suspend fun signUp(email: String, password: String, userData: Map<String, Any>): Boolean {
        return try {
            // Create user with email and password
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid

            if (userId != null) {
                // Save user data to Firestore
                firestore.collection("users").document(userId).set(userData).await()
                true // Return true if successful
            } else {
                false // Return false if user creation failed
            }
        } catch (e: Exception) {
            println("Sign-up failed: ${e.message}") // Debug print
            false // Return false if an exception occurs
        }
    }

    // Log in an existing user with email and password
    suspend fun login(email: String, password: String): Map<String, Any>? {
        return try {
            // Sign in with email and password
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user

            if (user != null) {
                // Fetch user data from Firestore
                val userData = firestore.collection("users").document(user.uid).get().await().data

                // Generate and add ID token to user data
                val idToken = user.getIdToken(true).await().token
                userData?.put("token", idToken)

                // Debug prints
                println("Generated ID token: $idToken")
                println("Auth result user UID: ${user.uid}")
                println("Auth result user email: ${user.email}")

                userData // Return user data with token
            } else {
                println("Login failed: User is null") // Debug print
                null // Return null if user is null
            }
        } catch (e: Exception) {
            println("Login failed: ${e.message}") // Debug print
            null // Return null if an exception occurs
        }
    }

    suspend fun logInWithGoogle(idToken: String): FirebaseUser? {
        return try {
            // Create the Google credential
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            // Sign in with the credential
            val authResult = auth.signInWithCredential(credential).await()

            // Check if the sign-in was successful
            if (authResult.user != null) {
                Log.d(TAG, "signInWithCredential:success")
                authResult.user // Return the signed-in user
            } else {
                Log.w(TAG, "signInWithCredential:failure - User is null")
                null // Return null if the user is not available
            }
        } catch (e: Exception) {
            // Log the error and return null
            Log.w(TAG, "signInWithCredential:failure", e)
            null
        }
    }

    // Fetch user data by user ID
    suspend fun getUserById(userId: String): Map<String, Any>? {
        return try {
            firestore.collection("users").document(userId).get().await().data
        } catch (e: Exception) {
            println("Failed to fetch user by ID: ${e.message}") // Debug print
            null // Return null if an exception occurs
        }
    }

    // Fetch the full user profile by user ID
    suspend fun getUserProfile(userId: String): User? {
        return try {
            firestore.collection("users").document(userId).get().await().data?.let { userData ->
                User(
                    uuid = userId,
                    firstName = userData["firstName"] as? String ?: "",
                    lastName = userData["lastName"] as? String ?: "",
                    phone = userData["phone"] as? String ?: "",
                    email = userData["email"] as? String ?: "",
                    password = userData["password"] as? String ?: ""
                )
            }
        } catch (e: Exception) {
            println("Failed to fetch user profile: ${e.message}") // Debug print
            null // Return null if an exception occurs
        }
    }

    // Get the current user's ID
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // Log out the current user
    suspend fun logout(context: Context) {
        try {
            // Sign out from Firebase Authentication
            auth.signOut()
            println("User signed out from Firebase") // Debug print

            // Clear stored token from preferences
            UserPreferences.deleteToken(context)
            println("Token deleted from preferences") // Debug print
        } catch (e: Exception) {
            println("Logout failed: ${e.message}") // Debug print
        }
    }
}