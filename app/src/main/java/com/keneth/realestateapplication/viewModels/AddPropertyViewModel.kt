package com.keneth.realestateapplication.viewModels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keneth.realestateapplication.data.Address
import com.keneth.realestateapplication.data.Amenities
import com.keneth.realestateapplication.data.ContactInfo
import com.keneth.realestateapplication.data.Property
import com.keneth.realestateapplication.data.PropertyType
import com.keneth.realestateapplication.enum.AddPropertyStep
import kotlinx.coroutines.launch
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
    var amenities by mutableStateOf(Amenities())
    var images = mutableStateListOf<Uri>()

    // State for tracking form submission
    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Move to the next step
    fun nextStep() {
        if (validateCurrentStep()) {
            currentStep = when (currentStep) {
                AddPropertyStep.BASIC_DETAILS -> AddPropertyStep.CONTACT_INFO
                AddPropertyStep.CONTACT_INFO -> AddPropertyStep.ADDRESS
                AddPropertyStep.ADDRESS -> AddPropertyStep.PROPERTY_TYPE
                AddPropertyStep.PROPERTY_TYPE -> AddPropertyStep.IMAGES
                AddPropertyStep.IMAGES -> AddPropertyStep.REVIEW
                AddPropertyStep.REVIEW -> AddPropertyStep.REVIEW
                AddPropertyStep.AMENITIES -> AddPropertyStep.AMENITIES
            }
        } else {
            errorMessage = "Please fill out all required fields."
        }
    }

    // Move to the previous step
    fun previousStep() {
        currentStep = when (currentStep) {
            AddPropertyStep.BASIC_DETAILS -> AddPropertyStep.BASIC_DETAILS
            AddPropertyStep.CONTACT_INFO -> AddPropertyStep.BASIC_DETAILS
            AddPropertyStep.ADDRESS -> AddPropertyStep.CONTACT_INFO
            AddPropertyStep.PROPERTY_TYPE -> AddPropertyStep.ADDRESS
            AddPropertyStep.IMAGES -> AddPropertyStep.PROPERTY_TYPE
            AddPropertyStep.REVIEW -> AddPropertyStep.IMAGES
            AddPropertyStep.AMENITIES -> AddPropertyStep.AMENITIES
        }
    }

    // Validate the current step
    private fun validateCurrentStep(): Boolean {
        return when (currentStep) {
            AddPropertyStep.BASIC_DETAILS -> title.isNotBlank() && description.isNotBlank() && price > 0
            AddPropertyStep.CONTACT_INFO -> contactInfo.name.isNotBlank() && contactInfo.phone.isNotBlank()
            AddPropertyStep.ADDRESS -> address.street.isNotBlank() && address.city.isNotBlank()
            AddPropertyStep.PROPERTY_TYPE -> propertyType.name.isNotBlank()
            AddPropertyStep.IMAGES -> images.isNotEmpty()
            AddPropertyStep.AMENITIES -> true
            AddPropertyStep.REVIEW -> true

        }
    }

    // Submit the form
    fun submitForm(onSuccess: () -> Unit) {
        if (!validateForm()) {
            errorMessage = "Please fill out all required fields."
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val propertyId = UUID.randomUUID().toString()
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
                    images = emptyList(),
                    contactInfo = contactInfo,
                    isFeatured = false,
                    isSold = false,
                    isListed = true,
                    listingDate = System.currentTimeMillis(),
                    updatedDate = System.currentTimeMillis(),
                    amenities = amenities
                )

                propertyViewModel.addProperty(property, images)
                isSuccess = true
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Failed to add property: ${e.message}"
                isSuccess = false
            } finally {
                isLoading = false
            }
        }
    }

    // Validate the entire form
    private fun validateForm(): Boolean {
        return title.isNotBlank() &&
                description.isNotBlank() &&
                price > 0 &&
                contactInfo.name.isNotBlank() &&
                contactInfo.phone.isNotBlank() &&
                address.street.isNotBlank() &&
                address.city.isNotBlank() &&
                propertyType.name.isNotBlank() &&
                images.isNotEmpty()
    }
}