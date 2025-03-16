package com.keneth.realestateapplication.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.keneth.realestateapplication.data.Appointment
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
    private val appointmentsCollection = firestore.collection("appointments")
    private val PROPERTIES_COLLECTION = "properties"
    private val propertiesCollection = firestore.collection("properties")
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
    suspend fun getAllProperties(): List<Property> {
        return try {
            val snapshot = firestore.collection("properties").get().await()
            snapshot.documents.mapNotNull { it.toObject(Property::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
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
    suspend fun getTotalProperties(): Int {
        return try {
            val snapshot = firestore.collection("properties").get().await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }
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

    suspend fun makeAppointment(
        propertyId: String,
        appointment: Appointment, // Pass the appointment object
        onResult: (Boolean, String?) -> Unit // Callback for success/failure
    ) {
        try {
            // Fetch the property details
            val property = getPropertyById(propertyId)
            if (property != null) {
                // Use the `plus` function to combine lists
                val updatedAppointments = property.appointments + appointment

                // Update the Firestore document
                propertiesCollection
                    .document(propertyId)
                    .update("appointments", updatedAppointments) // Update the appointments field
                    .await()

                // Call the callback with success
                onResult(true, "Appointment scheduled successfully!")
            } else {
                // Call the callback with failure
                onResult(false, "Property not found")
            }
        } catch (e: Exception) {
            // Call the callback with failure
            onResult(false, "Failed to make appointment: ${e.message}")
        }
    }

    // List a property (set listed = true)
    suspend fun listProperty(propertyId: String) {
        firestore.collection(PROPERTIES_COLLECTION)
            .document(propertyId)
            .update("listed", true)
            .await()
    }

    // Mark a property as sold
    suspend fun sellProperty(propertyId: String) {
        val updates = mapOf(
            "sold" to true, // Mark the property as sold
            "listed" to false // Mark the property as unlisted
        )

        firestore.collection(PROPERTIES_COLLECTION)
            .document(propertyId)
            .update(updates)
            .await()
    }


    suspend fun fetchAppointmentsForUser(userId: String): List<Appointment> {
        return try {
            val querySnapshot = appointmentsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            querySnapshot.toObjects(Appointment::class.java)
        } catch (e: Exception) {
            throw Exception("Failed to fetch appointments for user: ${e.message}")
        }
    }

    suspend fun fetchAppointmentsForProperty(propertyId: String): List<Appointment> {
        return try {
            val querySnapshot = appointmentsCollection
                .whereEqualTo("propertyId", propertyId)
                .get()
                .await()
            querySnapshot.toObjects(Appointment::class.java)
        } catch (e: Exception) {
            throw Exception("Failed to fetch appointments for property: ${e.message}")
        }
    }

    suspend fun fetchAllAppointments(): List<Appointment> {
        return try {
            val querySnapshot = appointmentsCollection.get().await()
            querySnapshot.toObjects(Appointment::class.java)
        } catch (e: Exception) {
            throw Exception("Failed to fetch all appointments: ${e.message}")
        }
    }
    suspend fun getPropertiesByUserId(userId: String): List<Property> {
        return try {
            val querySnapshot = firestore.collection("properties")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            querySnapshot.documents.mapNotNull { document ->
                document.toObject(Property::class.java)
            }
        } catch (e: Exception) {
            println("Error fetching properties by user ID: ${e.message}")
            emptyList()
        }
    }


}