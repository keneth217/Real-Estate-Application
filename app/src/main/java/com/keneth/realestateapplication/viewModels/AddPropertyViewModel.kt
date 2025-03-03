package com.keneth.realestateapplication.viewModels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.keneth.realestateapplication.data.Address
import com.keneth.realestateapplication.data.ContactInfo
import com.keneth.realestateapplication.data.Property
import com.keneth.realestateapplication.data.PropertyType
import com.keneth.realestateapplication.enum.AddPropertyStep
import java.util.UUID

class AddPropertyViewModel(
    private val propertyViewModel: PropertyViewModel
) : ViewModel() {

    // Current step in the form
    var currentStep by mutableStateOf(AddPropertyStep.BASIC_DETAILS)
        private set

    // Form data
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var price by mutableStateOf(0.0)
    var currency by mutableStateOf("KSh")
    var bedrooms by mutableStateOf(0)
    var bathrooms by mutableStateOf(0)
    var area by mutableStateOf(0.0)
    var areaUnit by mutableStateOf("sqm")
    var contactInfo by mutableStateOf(ContactInfo())
    var address by mutableStateOf(Address())
    var propertyType by mutableStateOf(PropertyType())
    var images = mutableStateListOf<Uri>() // Store image URIs for upload

    // State for tracking form submission
    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Move to the next step
    fun nextStep() {
        currentStep = when (currentStep) {
            AddPropertyStep.BASIC_DETAILS -> AddPropertyStep.CONTACT_INFO
            AddPropertyStep.CONTACT_INFO -> AddPropertyStep.ADDRESS
            AddPropertyStep.ADDRESS -> AddPropertyStep.PROPERTY_TYPE
            AddPropertyStep.PROPERTY_TYPE -> AddPropertyStep.IMAGES
            AddPropertyStep.IMAGES -> AddPropertyStep.REVIEW
            AddPropertyStep.REVIEW -> AddPropertyStep.REVIEW // Stay on review step
        }
    }

    // Move to the previous step
    fun previousStep() {
        currentStep = when (currentStep) {
            AddPropertyStep.BASIC_DETAILS -> AddPropertyStep.BASIC_DETAILS // Stay on first step
            AddPropertyStep.CONTACT_INFO -> AddPropertyStep.BASIC_DETAILS
            AddPropertyStep.ADDRESS -> AddPropertyStep.CONTACT_INFO
            AddPropertyStep.PROPERTY_TYPE -> AddPropertyStep.ADDRESS
            AddPropertyStep.IMAGES -> AddPropertyStep.PROPERTY_TYPE
            AddPropertyStep.REVIEW -> AddPropertyStep.IMAGES
        }
    }

    // Validate the form
    private fun validateForm(): Boolean {
        return when (currentStep) {
            AddPropertyStep.BASIC_DETAILS -> title.isNotBlank() && description.isNotBlank() && price > 0
            AddPropertyStep.CONTACT_INFO -> contactInfo.name.isNotBlank() && contactInfo.phone.isNotBlank()
            AddPropertyStep.ADDRESS -> address.street.isNotBlank() && address.city.isNotBlank()
            AddPropertyStep.PROPERTY_TYPE -> propertyType.name.isNotBlank()
            AddPropertyStep.IMAGES -> images.isNotEmpty()
            AddPropertyStep.REVIEW -> true // All steps are validated
        }
    }

    // Submit the form
    fun submitForm() {
        if (!validateForm()) {
            errorMessage = "Please fill out all required fields."
            return
        }

        isLoading = true
        errorMessage = null

        // Generate a unique ID for the property
        val propertyId = UUID.randomUUID().toString()

        // Create the Property object
        val property = Property(
            uuid = propertyId,
            title = title,
            description = description,
            price = price,
            currency = currency,
            propertyType = propertyType,
            bedrooms = bedrooms,
            bathrooms = bathrooms,
            area = area,
            areaUnit = areaUnit,
            address = address,
            images = emptyList(), // Will be updated after image upload
            contactInfo = contactInfo,
            isFeatured = false,
            isSold = false,
            isListed = true,
            listingDate = System.currentTimeMillis(),
            updatedDate = System.currentTimeMillis(),
            amenities = emptyList() // Add amenities if needed
        )

        // Upload images and add property
        propertyViewModel.addProperty(property, images)
    }
}