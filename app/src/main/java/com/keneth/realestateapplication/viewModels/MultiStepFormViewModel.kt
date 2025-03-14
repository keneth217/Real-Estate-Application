package com.keneth.realestateapplication.viewModels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keneth.realestateapplication.data.LanguagePreference
import com.keneth.realestateapplication.data.RealEstateUserRoles
import com.keneth.realestateapplication.data.UserAddress
import com.keneth.realestateapplication.data.User
import com.keneth.realestateapplication.data.UserPreferences
import com.keneth.realestateapplication.enum.RegistrationStep
import kotlinx.coroutines.launch
import java.util.UUID


class MultiStepFormViewModel(
    private val userViewModel: UserViewModel
) : ViewModel() {

    // Step management
    var currentStep by mutableStateOf(RegistrationStep.EMAIL_PASSWORD)
        private set

    // Form fields
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var phone by mutableStateOf("")
    var address by mutableStateOf(UserAddress())
    var profilePicture by mutableStateOf<Uri?>(null)
    var isEmailVerified by mutableStateOf(false)
    var isPhoneVerified by mutableStateOf(false)
    var createdAt by mutableStateOf(System.currentTimeMillis())
    var updatedAt by mutableStateOf(System.currentTimeMillis())
    var preferences by mutableStateOf(UserPreferences())
    var favorites by mutableStateOf<List<String>>(emptyList())
    var languagePreference by mutableStateOf(LanguagePreference.ENGLISH)

    // State management
    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)


    private val _selectedRoles = mutableStateOf<List<RealEstateUserRoles>>(emptyList())
    val selectedRoles: List<RealEstateUserRoles> get() = _selectedRoles.value


    fun selectRole(role: RealEstateUserRoles) {
        _selectedRoles.value = listOf(role)
    }


    fun isEmailPasswordStepValid(): Boolean {
        return email.isNotBlank() && password.isNotBlank()
    }

    fun isPersonalDetailsStepValid(): Boolean {
        return firstName.isNotBlank() && lastName.isNotBlank() && phone.isNotBlank()
    }


    fun nextStep() {
        when (currentStep) {
            RegistrationStep.EMAIL_PASSWORD -> {
                if (isEmailPasswordStepValid()) {
                    currentStep = RegistrationStep.PERSONAL_DETAILS
                } else {
                    errorMessage = "Please enter a valid email and password."
                }
            }

            RegistrationStep.PERSONAL_DETAILS -> {
                if (isPersonalDetailsStepValid()) {
                    currentStep = RegistrationStep.USER_ROLES
                } else {
                    errorMessage = "Please fill out all personal details."
                }
            }

            RegistrationStep.USER_ROLES -> {
                if (selectedRoles.isNotEmpty()) {
                    currentStep = RegistrationStep.ADDRESS
                } else {
                    errorMessage = "Please select at least one role."
                }
            }

            RegistrationStep.ADDRESS -> currentStep = RegistrationStep.PROFILE_PICTURE
            RegistrationStep.PROFILE_PICTURE -> currentStep = RegistrationStep.REVIEW
            RegistrationStep.REVIEW -> {}
        }
    }

    fun previousStep() {
        currentStep = when (currentStep) {
            RegistrationStep.EMAIL_PASSWORD -> RegistrationStep.EMAIL_PASSWORD
            RegistrationStep.PERSONAL_DETAILS -> RegistrationStep.EMAIL_PASSWORD
            RegistrationStep.USER_ROLES -> RegistrationStep.PERSONAL_DETAILS
            RegistrationStep.ADDRESS -> RegistrationStep.USER_ROLES
            RegistrationStep.PROFILE_PICTURE -> RegistrationStep.ADDRESS
            RegistrationStep.REVIEW -> RegistrationStep.PROFILE_PICTURE
        }
    }

    fun skipStep() {
        currentStep = when (currentStep) {
            RegistrationStep.ADDRESS -> RegistrationStep.PROFILE_PICTURE
            RegistrationStep.PROFILE_PICTURE -> RegistrationStep.REVIEW
            else -> currentStep
        }
    }
    fun submitForm(onSuccess: () -> Unit) {
        if (!validateForm()) {
            errorMessage = "Please fill out all required fields."
            println("MultiStepFormViewModel: Form validation failed")
            return
        }

        if (selectedRoles.isEmpty()) {
            errorMessage = "Please select at least one role."
            println("MultiStepFormViewModel: No role selected")
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                println("MultiStepFormViewModel: Starting form submission")

                val profileImageUri = profilePicture
                val profileImageUrl = profileImageUri?.toString() ?: ""
                println("MultiStepFormViewModel: Profile image URI: $profileImageUri")

                val user = User(
                    uuid = UUID.randomUUID().toString(),
                    firstName = firstName,
                    lastName = lastName,
                    phone = phone,
                    email = email,
                    password = password,
                    userRole = selectedRoles,
                    profileImage = profileImageUrl,
                    address = address,
                    isEmailVerified = isEmailVerified,
                    isPhoneVerified = isPhoneVerified,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                    preferences = preferences,
                    favorites = favorites,
                    languagePreference = languagePreference
                )

                println("MultiStepFormViewModel: User data to save: ${user.toMap()}")

                if (profileImageUri != null) {
                    println("MultiStepFormViewModel: Calling signUp in UserViewModel")
                    userViewModel.signUp(email, password, user.toMap(), profileImageUri)
                } else {
                    println("MultiStepFormViewModel: No profile image provided")
                }

                // Set success state
                isSuccess = true
                onSuccess()
                println("MultiStepFormViewModel: Registration success: $user")
            } catch (e: Exception) {
                errorMessage = "Failed to register user: ${e.message}"
                isSuccess = false
                println("MultiStepFormViewModel: Error during form submission: ${e.message}")
                e.printStackTrace() // Print the full stack trace for debugging
            } finally {
                isLoading = false
                println("MultiStepFormViewModel: Form submission completed")
            }
        }
    }
    private fun validateForm(): Boolean {
        return email.isNotBlank() &&
                password.isNotBlank() &&
                firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                phone.isNotBlank()

    }
}