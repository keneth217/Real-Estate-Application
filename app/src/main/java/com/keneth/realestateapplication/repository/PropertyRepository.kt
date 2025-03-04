package com.keneth.realestateapplication.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.keneth.realestateapplication.data.Property
import com.keneth.realestateapplication.data.PropertyType
import com.keneth.realestateapplication.data.User
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PropertyRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {
    // Upload a single image and return its URL
    suspend fun uploadImage(imageUri: Uri): String? {
        return try {
            val imageRef = storage.reference.child("property_images/${UUID.randomUUID()}.jpg")
            imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            println("Error uploading image: ${e.message}")
            null
        }
    }

    // Upload multiple images and return their URLs
    suspend fun uploadImages(imageUris: List<Uri>): List<String> {
        return imageUris.mapNotNull { uploadImage(it) }
    }

    // Add a new property with images
    suspend fun addProperty(property: Property, imageUris: List<Uri>): Boolean {
        return try {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val propertyId = UUID.randomUUID().toString()

                // Upload images and get URLs
                val imageUrls = uploadImages(imageUris)

                // Create a new Property with image URLs
                val propertyWithImages = property.copy(
                    uuid = propertyId,
                    images = imageUrls
                )

                // Save the property to Firestore
                firestore.collection("properties")
                    .document(propertyId)
                    .set(propertyWithImages)
                    .await()

                true // Success
            } else {
                false // User not logged in
            }
        } catch (e: Exception) {
            println("Error adding property: ${e.message}")
            false // Failure
        }
    }

    // Get all properties from Firestore
    suspend fun getAllProperties(): List<Property> {
        return try {
            val snapshot = firestore.collection("properties").get().await()
            snapshot.documents.mapNotNull { it.toObject(Property::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Get all listed properties (not sold)
    suspend fun listProperties(): List<Property> {
        return try {
            val snapshot = firestore.collection("properties")
                .whereEqualTo("listed", true)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(Property::class.java) }
        } catch (e: Exception) {
            println("Error fetching listed properties: ${e.message}")
            emptyList()
        }
    }

    // Get all sold properties
    suspend fun listSoldProperties(): List<Property> {
        return try {
            val snapshot = firestore.collection("properties")
                .whereEqualTo("sold", true)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(Property::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Get the total number of properties
    suspend fun getTotalProperties(): Int {
        return try {
            val snapshot = firestore.collection("properties").get().await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    // Get the total number of sold properties
    suspend fun getTotalSoldProperties(): Int {
        return try {
            val snapshot = firestore.collection("properties")
                .whereEqualTo("sold", true)
                .get()
                .await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    // Get total sales amount
    suspend fun getTotalSalesAmount(): Double {
        return try {
            val snapshot = firestore.collection("properties")
                .whereEqualTo("sold", true)
                .get()
                .await()
            snapshot.documents.sumOf { it.getDouble("price") ?: 0.0 }
        } catch (e: Exception) {
            0.0
        }
    }

    // Get total listed properties (not sold)
    suspend fun getTotalListedProperties(): Int {
        return try {
            val snapshot = firestore.collection("properties")
                .whereEqualTo("listed", true)
                .get()
                .await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    // Add property type
    suspend fun addPropertyType(propertyType: PropertyType) {
        try {
            firestore.collection("propertytype").add(propertyType).await()
        } catch (e: Exception) {
            println("Error adding property type: ${e.message}")
        }
    }

    // Get property types
    suspend fun getPropertyType(): List<PropertyType> {
        return try {
            firestore.collection("propertytype").get()
                .await()
                .toObjects(PropertyType::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPropertyById(propertyId: String): Property? {
        return try {
            val snapshot = firestore.collection("properties").document(propertyId).get().await()
            snapshot.toObject(Property::class.java)
        } catch (e: Exception) {
            println("Error fetching property by ID: ${e.message}")
            null
        }
    }
}