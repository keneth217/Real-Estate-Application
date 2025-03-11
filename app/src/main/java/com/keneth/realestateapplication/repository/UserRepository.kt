package com.keneth.realestateapplication.repository

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.keneth.realestateapplication.UserPreferences
import com.keneth.realestateapplication.data.RealEstateUserRoles
import com.keneth.realestateapplication.data.UserAddress
import com.keneth.realestateapplication.data.User
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UserRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {
    suspend fun uploadImage(imageUri: Uri): String? {
        return try {
            val imageRef = storage.reference.child("users_profiles/${UUID.randomUUID()}.jpg")
            imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun signUp(
        email: String,
        password: String,
        userData: Map<String, Any>,
        imageUri: Uri
    ): Boolean {
        return try {
            // Step 1: Create user in Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid

            if (userId != null) {
                println("User created in Firebase Authentication with UID: $userId")

                // Step 2: Upload profile picture to Firebase Storage (if provided)
                val imageUrl = if (imageUri != null) {
                    uploadImage(imageUri)
                } else {
                    null
                }

                println("Profile picture URL: $imageUrl")

                // Step 3: Save user data to Firestore
                val updatedUserData = userData.toMutableMap().apply {
                    put("profileImage", imageUrl ?: "")
                }

                println("User data to save in Firestore: $updatedUserData")

                // Save the user data to Firestore
                firestore.collection("users").document(userId).set(updatedUserData).await()
                println("User data saved to Firestore")
                true // Return true if everything succeeds
            } else {
                println("User creation failed: UID is null")
                false // Return false if user creation fails
            }
        } catch (e: Exception) {
            println("Sign-up failed: ${e.message}")
            throw e // Rethrow the exception to propagate the exact error
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

    suspend fun getUserProfile(userId: String): User? {
        return try {
            firestore.collection("users").document(userId).get().await().data?.let { userData ->
                val addressMap = userData["address"] as? Map<String, Any>
                val userAddress = addressMap?.let { map ->
                    UserAddress(
                        street = map["street"] as? String ?: "",
                        city = map["city"] as? String ?: "",
                        postalCode = map["postalCode"] as? String ?: "",
                        country = map["country"] as? String ?: ""
                    )
                } ?: UserAddress()
                val userRolesList =
                    (userData["userRole"] as? List<String>)?.mapNotNull { roleName ->
                        RealEstateUserRoles.entries.find { it.name == roleName }
                    } ?: emptyList()
                User(
                    uuid = userId,
                    firstName = userData["firstName"] as? String ?: "",
                    lastName = userData["lastName"] as? String ?: "",
                    phone = userData["phone"] as? String ?: "",
                    email = userData["email"] as? String ?: "",
                    password = userData["password"] as? String ?: "",
                    profileImage = userData["profileImage"] as? String ?: "",
                    address = userAddress,
                    userRole = userRolesList // Correctly mapped userRole
                )
            }
        } catch (e: Exception) {
            println("Failed to fetch user profile: ${e.message}") // Debug print
            null
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
    suspend fun updateUserProfile(userId: String, userData: Map<String, Any>) {
        try {
            firestore.collection("users").document(userId).set(userData).await()
            println("User profile updated successfully")
        } catch (e: Exception) {
            println("Failed to update user profile: ${e.message}")
            throw e
        }
    }
}