package com.keneth.realestateapplication.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.keneth.realestateapplication.data.LanguagePreference
import com.keneth.realestateapplication.data.RealEstateUserRoles
import com.keneth.realestateapplication.data.USerAddress
import com.keneth.realestateapplication.data.User
import com.keneth.realestateapplication.data.UserPreferences
import com.keneth.realestateapplication.enum.RegistrationStep

class MultiStepFormViewModel(
    private val userViewModel: UserViewModel // Pass UserViewModel as a dependency
) : ViewModel() {
    // Current step in the form
    var currentStep by mutableStateOf(RegistrationStep.EMAIL_PASSWORD)
        private set

    // Form data
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var phone by mutableStateOf("")
    var address by mutableStateOf(USerAddress())
    var profilePicture by mutableStateOf<String?>(null)

    // Additional fields from the User class
    var userRole by mutableStateOf(RealEstateUserRoles.GUEST)
    var isEmailVerified by mutableStateOf(false)
    var isPhoneVerified by mutableStateOf(false)
    var createdAt by mutableStateOf(System.currentTimeMillis())
    var updatedAt by mutableStateOf(System.currentTimeMillis())
    var preferences by mutableStateOf(UserPreferences())
    var favorites by mutableStateOf<List<String>>(emptyList())
    var languagePreference by mutableStateOf(LanguagePreference.ENGLISH)

    // Validation functions
    fun isEmailPasswordStepValid(): Boolean {
        return email.isNotBlank() && password.isNotBlank()
    }

    fun isPersonalDetailsStepValid(): Boolean {
        return firstName.isNotBlank() && lastName.isNotBlank() && phone.isNotBlank()
    }

    // Move to the next step
    fun nextStep() {
        when (currentStep) {
            RegistrationStep.EMAIL_PASSWORD -> {
                if (isEmailPasswordStepValid()) {
                    currentStep = RegistrationStep.PERSONAL_DETAILS
                }
            }

            RegistrationStep.PERSONAL_DETAILS -> {
                if (isPersonalDetailsStepValid()) {
                    currentStep = RegistrationStep.ADDRESS
                }
            }

            RegistrationStep.ADDRESS -> currentStep = RegistrationStep.PROFILE_PICTURE
            RegistrationStep.PROFILE_PICTURE -> currentStep = RegistrationStep.REVIEW
            RegistrationStep.REVIEW -> {} // Stay on review step
        }
    }

    // Move to the previous step
    fun previousStep() {
        currentStep = when (currentStep) {
            RegistrationStep.EMAIL_PASSWORD -> RegistrationStep.EMAIL_PASSWORD // Stay on first step
            RegistrationStep.PERSONAL_DETAILS -> RegistrationStep.EMAIL_PASSWORD
            RegistrationStep.ADDRESS -> RegistrationStep.PERSONAL_DETAILS
            RegistrationStep.PROFILE_PICTURE -> RegistrationStep.ADDRESS
            RegistrationStep.REVIEW -> RegistrationStep.PROFILE_PICTURE
        }
    }

    // Skip a step
    fun skipStep() {
        currentStep = when (currentStep) {
            RegistrationStep.ADDRESS -> RegistrationStep.PROFILE_PICTURE
            RegistrationStep.PROFILE_PICTURE -> RegistrationStep.REVIEW
            else -> currentStep // Do nothing for other steps
        }
    }

    // Submit the form
    fun submitForm() {
        // Create the User object
        val user = User(
            uuid = "", // Generate or fetch UUID as needed
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            email = email,
            password = password,
            userRole = userRole,
            profileImage = profilePicture ?: "", // Use an empty string if profilePicture is null
            address = address,
            isEmailVerified = isEmailVerified,
            isPhoneVerified = isPhoneVerified,
            createdAt = createdAt,
            updatedAt = updatedAt,
            preferences = preferences,
            favorites = favorites,
            languagePreference = languagePreference
        )

        // Call the signUp function in UserViewModel
        userViewModel.signUp(email, password, user.toMap())
        println("Registration success $user")
    }
}